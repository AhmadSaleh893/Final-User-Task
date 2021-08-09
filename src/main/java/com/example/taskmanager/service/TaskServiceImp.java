package com.example.taskmanager.service;

import com.example.taskmanager.ApiError;
import com.example.taskmanager.repository.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.JwtUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

import static com.example.taskmanager.security.TOKEN_GETTER.TOKEN;
import static com.example.taskmanager.security.TOKEN_GETTER.localDateTime;


@JsonIgnoreProperties(value={"user"})
@Service
public class TaskServiceImp implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    public static final Logger log = LoggerFactory.getLogger(UserServiceImp.class);
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String message = "";
    String path = "";
    String error = "";

    @Autowired
    public TaskServiceImp(TaskRepository taskRepository
            , UserRepository userRepository
            , JwtUtils jwtUtils) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public ResponseEntity<ApiError> createTask(@RequestBody Task task) {

        String email = jwtUtils.extractEmail(TOKEN);
        Optional<User> user = userRepository.findUserByEmail(email);
        Long id = user.get().getId();
        task.setUser(userRepository.findById(id).get());
        taskRepository.save(task);

        return new ResponseEntity(task, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiError> deleteTask(@PathVariable Long id) {
        path = "/task/{id}";
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {

            String email = jwtUtils.extractEmail(TOKEN);
            Optional<User> userFromToken = userRepository.findUserByEmail(email);
            Long userId = task.get().getUser().getId();
            Optional<User> userFromTask = userRepository.findById(userId);
            if (userFromTask.get().getId() == userFromToken.get().getId()) {

                try {
                    taskRepository.deleteById(id);
                    return new ResponseEntity(task + "\nRecord has been deleted", HttpStatus.OK);
                } catch (Exception f) {

                    status = HttpStatus.CONFLICT;
                    error = "Error";
                    message = "Some error";
                    return new ResponseEntity(new ApiError(localDateTime
                            , status
                            , error
                            , message
                            , path), status);
                }
            } else {

                status = HttpStatus.UNAUTHORIZED;
                error = "UNAUTHORIZED";
                message = "This Task not belong to this user";
                return new ResponseEntity(new ApiError(localDateTime
                        , status
                        , error
                        , message
                        , path), status);
            }
        } else {

            status = HttpStatus.NOT_FOUND;
            error = "NOT_FOUND";
            message = "This Task not exist";
            return new ResponseEntity(new ApiError(localDateTime
                    , status
                    , error
                    , message
                    , path), status);
        }
    }

    @Override
    public ResponseEntity<ApiError> updateTask(@RequestBody Task newTask, @PathVariable Long id) {
        path = "/task/{id}";
        // We have to compare if the user token is the same as the user task.
        Optional<Task> myTask = taskRepository.findById(id);

        if (myTask.isEmpty()) {

            status = HttpStatus.NOT_FOUND;
            error = "Not found";
            message = "This user is not available";
            return new ResponseEntity(new ApiError(localDateTime
                    , status
                    , error
                    , message
                    , path), status);
        }

        String email = jwtUtils.extractEmail(TOKEN);
        Optional<User> userFromToken = userRepository.findUserByEmail(email);
        User userFromTask = myTask.get().getUser();

        if (userFromTask.getId() == userFromToken.get().getId()) {
            try {
                myTask.map(task -> {
                    task.setDescription(newTask.getDescription());
                    task.setCompleted(newTask.getCompleted());

                    return taskRepository.save(task);
                })
                        .orElseGet(() -> {
                            return taskRepository.save(newTask);
                        });
                status = HttpStatus.OK;

                return new ResponseEntity(myTask, status);

            } catch (Exception c) {
                log.error("error in updating task");
                message = "error in updating task";
                error = "error in updating task";
                return new ResponseEntity(new ApiError(localDateTime
                        , status
                        , error
                        , message
                        , path), status);
            }


        } else {
            status = HttpStatus.UNAUTHORIZED;
            error = "UNAUTHORIZED";
            message = "This Task not belongs to this user";

            return new ResponseEntity(new ApiError(localDateTime
                    , status
                    , error
                    , message
                    , path), status);

        }
    }

    @Override
    public ResponseEntity<ApiError> getTasks() {
        path = "/user";
        String email = jwtUtils.extractEmail(TOKEN);
        Optional<User> user = userRepository.findUserByEmail(email);

        if (taskRepository.count() != 0) {
            return new ResponseEntity(taskRepository.findTasksByUserId(user.get().getId()), HttpStatus.OK);
        } else {
            error = "Empty";
            message = "Nothing to present";

            return new ResponseEntity(new ApiError(localDateTime
                    , status
                    , error
                    , message
                    , path), status);
        }

    }
}
