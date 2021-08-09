package com.example.taskmanager.service;

import com.example.taskmanager.ApiError;
import com.example.taskmanager.repository.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;


public interface TaskService {

    ResponseEntity<ApiError> createTask(@RequestBody Task task);

    ResponseEntity<ApiError> deleteTask(Long id);

    ResponseEntity<ApiError> updateTask(Task task,Long id);

    Object getTasks();
}
