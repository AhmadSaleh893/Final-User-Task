package com.example.taskmanager.service;

import com.example.taskmanager.ApiError;
import com.example.taskmanager.repository.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.JwtUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;

import static com.example.taskmanager.security.TOKEN_GETTER.*;

@Service
@Data
public class UserServiceImp extends ResponseEntityExceptionHandler implements UserService {

    private final UserRepository userRepository;
    public static final Logger log = LoggerFactory.getLogger(UserServiceImp.class);
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String error = "Missing field";
    String message = "";
    String path = "";

    @Autowired
    public UserServiceImp(UserRepository userRepository
            , JwtUtils jwtUtils
            , PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    public Boolean isFitBaseAttributes(User user) {
        if (user.getEmail() == null) {
            return false;
        }
        else if (user.getPassword() == null) {
            return false;
        }
        return true;
    }

    @Override
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        path = "/user/{id}";
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {

            String sourceEmail = jwtUtils.extractEmail(TOKEN.trim());
            String destinationEmail = user.get().getEmail();//User to be deleted
            if (sourceEmail.equals(destinationEmail)) {
                if (user.get().getTasks().size() > 0) {
                    status = HttpStatus.CONFLICT;
                    error = "Error with task entity";
                    message = "Can't delete this user because it has tasks depends on it";
                    return new ResponseEntity(new ApiError(localDateTime
                            , status
                            , error
                            , message
                            , path), status);
                }
                userRepository.deleteById(id);
                return new ResponseEntity(user + "\nThis record has deleted", HttpStatus.OK);

            } else {
                status = HttpStatus.UNAUTHORIZED;
                error = "NOT AUTHORIZED";
                message = "Sorry you just can delete your self";
                return new ResponseEntity<>(new ApiError(localDateTime
                        , status
                        , error
                        , message
                        , path), status);
            }
        } else {
            status = HttpStatus.NOT_FOUND;
            error = "NOT EXIST";
            message = "Sorry this id you entered is not exist";
            return new ResponseEntity<>(new ApiError(localDateTime
                    , status
                    , error
                    , message
                    , path), status);
        }
    }

    @Override
    public ResponseEntity<ApiError> updateUser(@PathVariable Long id
            , @RequestBody User newUser) {
        path="/user/{id}";
        Optional<User> destinationUser = userRepository.findById(id);
        if (!destinationUser.isPresent()) {
            throw new UsernameNotFoundException("incorrect email or password");
        }
        String email = jwtUtils.extractEmail(TOKEN);


        if (email.equals(destinationUser.get().getEmail())) {
            try {
                if (isFitBaseAttributes(newUser) == false) {
                    log.error("Type of Objects not right");
                    message = "wrong wording";
                    error="Object name error";
                    status =   HttpStatus.BAD_REQUEST;
                    return new ResponseEntity(new ApiError(localDateTime
                            ,status
                            , error
                            , message
                            , path), status);
                }
                if (newUser.getEmail().trim().equals("") || newUser.getPassword().trim().equals("")) {
                    log.error("you should fill the email and password");
                    message = "Email and password are required fields*";
                    error = "NO NULL";
                    status = HttpStatus.INTERNAL_SERVER_ERROR;
                    return new ResponseEntity(new ApiError(localDateTime,
                            status
                            , error
                            , message
                            , path), status);
                } else {

                    if (userRepository.findUserByEmail(newUser.getEmail()).isEmpty()) {

                        destinationUser.map(user -> {
                            user.setName(newUser.getName());
                            user.setAge(newUser.getAge());
                            user.setEmail(newUser.getEmail());
                            user.setPassword(passwordEncoder.encode(newUser.getPassword()));
                            user.setActive(1);
                            user.setRoles("ROLE_USER");

                            return userRepository.save(user);
                        })
                                .orElseGet(() -> {
                                    return userRepository.save(newUser);
                                });
                        status = HttpStatus.OK;
                        return new ResponseEntity(destinationUser, status);
                    } else {

                        log.error("The email you entered, is exist for another user");
                        message = "The email you entered is exist before";
                        error = "Uniqueness issue";
                        return new ResponseEntity(new ApiError(localDateTime
                                , status
                                , error
                                , message
                                , path), status);
                    }
                }

            } catch (Exception fg) {
                log.error("Bad id input");
                message = "Bad id input";
                error = "Bad id input";
                status = HttpStatus.BAD_REQUEST;
                return new ResponseEntity(new ApiError(localDateTime
                        , status
                        , error
                        , message
                        , path), status);
            }
        } else {
            message = "Sorry you just can Update your self";
            error = "NOT AUTHORIZED";
            status = HttpStatus.UNAUTHORIZED;
            return new ResponseEntity<>(new ApiError(localDateTime
                    , status
                    , error
                    , message,
                    path), status);
        }
    }
}



