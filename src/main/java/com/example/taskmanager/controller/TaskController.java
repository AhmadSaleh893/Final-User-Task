package com.example.taskmanager.controller;

import com.example.taskmanager.repository.Task;
import com.example.taskmanager.ApiError;
import com.example.taskmanager.service.TaskServiceImp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@JsonIgnoreProperties(value={"user"})
@RequestMapping("/task")
@NoArgsConstructor(force = true)
public class TaskController {
    @Autowired
    private final TaskServiceImp taskServiceImp;

    @PostMapping()
    private ResponseEntity<ApiError> createTask(@RequestBody Task task)
    {
        return taskServiceImp.createTask(task);
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<ApiError> deleteTask(@PathVariable Long id)
    {
        return taskServiceImp.deleteTask(id);
    }

    @PutMapping("/{id}")
    private ResponseEntity<ApiError> updateTask(@RequestBody Task task, @PathVariable Long id)
    {
        return taskServiceImp.updateTask(task,id);
    }
    //It returns all tasks belongs for this user
    @GetMapping()
    private Object getAllTasks()
    {
        return taskServiceImp.getTasks();
    }
}
