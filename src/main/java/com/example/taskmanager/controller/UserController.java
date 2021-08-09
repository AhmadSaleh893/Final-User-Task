package com.example.taskmanager.controller;

import com.example.taskmanager.repository.User;
import com.example.taskmanager.ApiError;
import com.example.taskmanager.service.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserServiceImp userServiceImp;

    @Autowired
    public UserController(UserServiceImp userServiceImp)
    {
        this.userServiceImp = userServiceImp;
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userServiceImp.deleteUser(id);
    }

    @PutMapping("/{id}")
    private ResponseEntity<ApiError> updateUser( @PathVariable Long id,@RequestBody User user) {
        return userServiceImp.updateUser(id,user);
    }





}
