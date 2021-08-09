package com.example.taskmanager.controller;

import com.example.taskmanager.security.AuthenticationRequestModel;
import com.example.taskmanager.service.MyUserAuthenticationServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAuthenticationController {

    @Autowired
    private MyUserAuthenticationServiceImp myUserAuthenticationService;

    @PostMapping("/signup")
    private ResponseEntity<?> signUp(@RequestBody AuthenticationRequestModel user) throws Exception {
        return myUserAuthenticationService.signUp(user);
    }

    @PostMapping("/login")
    private ResponseEntity<?> login(@RequestBody AuthenticationRequestModel user) throws Exception {
        return myUserAuthenticationService.login(user);
    }
}
