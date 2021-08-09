package com.example.taskmanager.service;

import com.example.taskmanager.security.AuthenticationRequestModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface MyUserAuthenticationService {

    ResponseEntity<?> login(@RequestBody AuthenticationRequestModel user)throws Exception ;

    ResponseEntity<?> signUp(@RequestBody AuthenticationRequestModel user)throws Exception ;
}
