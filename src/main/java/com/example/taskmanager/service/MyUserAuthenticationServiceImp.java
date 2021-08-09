package com.example.taskmanager.service;

import com.example.taskmanager.ApiError;
import com.example.taskmanager.repository.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.AuthenticationRequestModel;
import com.example.taskmanager.security.AuthenticationResponseModel;
import com.example.taskmanager.security.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

import static com.example.taskmanager.security.TOKEN_GETTER.localDateTime;


@Service
public class MyUserAuthenticationServiceImp implements MyUserAuthenticationService{

    private MyUserDetailsService myUserDetailsService;
    private JwtUtils utils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public MyUserAuthenticationServiceImp(MyUserDetailsService myUserDetailsService
            , JwtUtils utils
            , UserRepository userRepository
            , PasswordEncoder passwordEncoder) {
        this.myUserDetailsService = myUserDetailsService;
        this.utils = utils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<?> signUp(@RequestBody AuthenticationRequestModel user) throws Exception {
        if (user.getEmail().trim().equals("") || user.getPassword().trim().equals("")) {
            return new ResponseEntity(new ApiError(localDateTime
                    , HttpStatus.BAD_REQUEST
                    , "email or password can't be empty"
                    , "NOT NULL", "/signup"), HttpStatus.BAD_REQUEST);
        }

        Optional<User> user1 = userRepository.findUserByEmail(user.getEmail());
        if (user1.isPresent()) {
            return new ResponseEntity<>(new ApiError(localDateTime
                    , HttpStatus.CONFLICT
                    , "EXISTENCE ERROR"
                    , "Sorry this email is exist for another user"
                    , "/signup"), HttpStatus.UNAUTHORIZED);
        } else {
            user.setEmail(user.getEmail().trim());
            user.setName(user.getName().trim());

            myUserDetailsService.createUser(user);
            final UserDetails userDetails = myUserDetailsService.loadUserByUsername(user.getEmail());
            final String token = utils.generateToken(userDetails);
            return ResponseEntity.ok(new AuthenticationResponseModel(token));
        }
    }

    @Override
    public ResponseEntity<?> login(@RequestBody AuthenticationRequestModel user) throws Exception {
        Optional<User> user1 = userRepository.findUserByEmail(user.getEmail());

        if (user1.isPresent() == false) {
            throw new UsernameNotFoundException("Incorrect username or password");
        }

        if (passwordEncoder.matches(user.getPassword(), user1.get().getPassword())) {
            final UserDetails userDetails = myUserDetailsService.loadUserByUsername(user.getEmail());
            final String token = utils.generateToken(userDetails);
            return ResponseEntity.ok(new AuthenticationResponseModel(token));
        } else {
            String error = "BAD INPUT";
            String message = "This password is incorrect";
            String path = "/login";
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return new ResponseEntity(new ApiError(localDateTime
                    , status
                    , error
                    , message
                    , path), status);
        }
    }

}
