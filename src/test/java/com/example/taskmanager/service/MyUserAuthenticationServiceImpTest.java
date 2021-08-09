package com.example.taskmanager.service;

import com.example.taskmanager.ApiError;
import com.example.taskmanager.repository.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.AuthenticationRequestModel;
import com.example.taskmanager.security.AuthenticationResponseModel;
import com.example.taskmanager.security.JwtUtils;
import com.example.taskmanager.security.MyUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static com.example.taskmanager.security.TOKEN_GETTER.localDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {MyUserAuthenticationServiceImp.class, JwtUtils.class, MyUserDetailsService.class})
@ExtendWith(SpringExtension.class)
public class MyUserAuthenticationServiceImpTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MyUserAuthenticationServiceImp myUserAuthenticationServiceImp;

    @MockBean
    private MyUserDetailsService myUserDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    private User user1 = new User(1L, "Ahmad", "Ahmad@hotmail", "2513", 1, "ROLE_USER", 21);
    private User user2 = new User(2L, "Samer", "Samer@hotmail", "1234", 1, "ROLE_USER", 25);

    @Test()
    public void testSignup_emptyEmailOrPassword() throws Exception {
        User emptyUser = new User();
        emptyUser.setPassword("");
        emptyUser.setEmail("");
        AuthenticationRequestModel user = new AuthenticationRequestModel(emptyUser);
        assertEquals(new ResponseEntity(new ApiError(localDateTime
                , HttpStatus.BAD_REQUEST
                , "email or password can't be empty"
                , "NOT NULL"
                , "/signup"), HttpStatus.BAD_REQUEST), myUserAuthenticationServiceImp.signUp(user));
    }

    @Test()
    public void testSignup_notAuthorized() throws Exception {
        AuthenticationRequestModel existsUser = new AuthenticationRequestModel(user1);
        when(userRepository.findUserByEmail(existsUser.getEmail())).thenReturn(Optional.of(user1));
        assertEquals(new ResponseEntity<>(new ApiError(localDateTime
                , HttpStatus.CONFLICT, "EXISTENCE ERROR"
                , "Sorry this email is exist for another user"
                , "/signup"), HttpStatus.UNAUTHORIZED), myUserAuthenticationServiceImp.signUp(existsUser));
        verify(userRepository).findUserByEmail(existsUser.getEmail());
    }

    @Test()
    public void testSignup() throws Exception {
        //Everything alright
        User user2 = new User(2L, "Samer", "Samer@hotmail", "1234", 1, "ROLE_USER", 25);
        AuthenticationRequestModel validUser = new AuthenticationRequestModel(user2);
        when(userRepository.findUserByEmail(user2.getEmail())).thenReturn(Optional.empty());
        when(myUserDetailsService.createUser(validUser)).thenReturn(user2);

        when(myUserDetailsService.loadUserByUsername(user2.getEmail())).thenReturn(new MyUserDetails(user2));

        assertEquals(validUser.getEmail()
                , (jwtUtils.extractEmail(((AuthenticationResponseModel) myUserAuthenticationServiceImp
                                .signUp(validUser).getBody()).getToken())));

        verify(userRepository).findUserByEmail(validUser.getEmail());
        verify(myUserDetailsService).createUser(validUser);
        verify(myUserDetailsService).loadUserByUsername(validUser.getEmail());
    }

    @Test
    public void testLogin_userNotFoundException() {
        AuthenticationRequestModel user = new AuthenticationRequestModel(user1);
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> myUserAuthenticationServiceImp.login(user));

        verify(userRepository).findUserByEmail(user.getEmail());
    }

    @Test
    public void testLogin_incorrectPassword() throws Exception {
        AuthenticationRequestModel user = new AuthenticationRequestModel(user1);
        when(userRepository.findUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(passwordEncoder.matches(user1.getPassword(), user1.getPassword())).thenReturn(false);
        String error = "BAD INPUT";
        String message = "This password is incorrect";
        String path = "/login";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        assertEquals(new ResponseEntity(new ApiError(localDateTime
                        , status
                        , error
                        , message
                        , path), status), myUserAuthenticationServiceImp.login(user));

        verify(userRepository).findUserByEmail(user.getEmail());
        verify(passwordEncoder).matches(user1.getPassword(), user1.getPassword());
    }

    @Test
    public void testLogin() throws Exception {
        //Everything alright
        AuthenticationRequestModel user = new AuthenticationRequestModel(user1);
        when(userRepository.findUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(passwordEncoder.matches(user1.getPassword(), user1.getPassword())).thenReturn(true);
        when(myUserDetailsService.loadUserByUsername(user1.getEmail())).thenReturn(new MyUserDetails(user1));

        assertEquals(user1.getEmail(), jwtUtils.extractEmail(((AuthenticationResponseModel)
                myUserAuthenticationServiceImp
                        .login(user).getBody()).getToken()));
        verify(userRepository).findUserByEmail(user.getEmail());
        verify(passwordEncoder).matches(user1.getPassword(), user1.getPassword());
        verify(myUserDetailsService).loadUserByUsername(user1.getEmail());
    }

}

