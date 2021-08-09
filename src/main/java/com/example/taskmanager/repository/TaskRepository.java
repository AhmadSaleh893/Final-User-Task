package com.example.taskmanager.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task,Long> {

    List<Task> findTasksByUserId(Long Id);
}
