package com.example.taskmanager.service;

import com.example.taskmanager.ApiError;
import com.example.taskmanager.repository.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public interface UserService {

    ResponseEntity<?> deleteUser(Long id);

    ResponseEntity<ApiError> updateUser(@PathVariable Long id, @RequestBody User user);

}
