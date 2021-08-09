package com.example.taskmanager.service;


import com.example.taskmanager.ApiError;
import com.example.taskmanager.repository.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.JwtUtils;
import com.example.taskmanager.security.MyUserDetails;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.taskmanager.security.TOKEN_GETTER.TOKEN;
import static com.example.taskmanager.security.TOKEN_GETTER.localDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {JwtUtils.class, TaskServiceImp.class})
@ExtendWith(SpringExtension.class)
public class TaskServiceImpTest {

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private TaskRepository taskRepository;

    @Autowired
    private TaskServiceImp taskServiceImp;

    @MockBean
    private UserRepository userRepository;

    private static String saveToken;
    private User user1 = new User(1L, "Ahmad", "Ahmad@hotmail", "2513", 1, "ROLE_USER", 21);
    private User user2 = new User(2L, "Samer", "Samer@hotmail", "1234", 1, "ROLE_USER", 25);
    private Task task1 = new Task(1L, "user1 desc", true);
    private Task task2 = new Task(2L, "user2 desc", true);

    @BeforeAll
    static void tokenSaver() {
        saveToken = TOKEN;
    }

    @BeforeEach
    void setUp() {
        localDateTime = LocalDateTime.now();
    }

    @AfterEach
    void atTheEnd() {
        TOKEN = "";
    }

    @AfterAll
    static void tokenRelease() {
        TOKEN = saveToken;
    }

    @Test
    public void testCreateTask() {
        task1.setUser(user1);
        TOKEN = jwtUtils.generateToken(new MyUserDetails(user1));
        when(userRepository.findUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(taskRepository.save(task1)).thenReturn(task1);
        assertEquals(new ResponseEntity(task1, HttpStatus.OK), taskServiceImp.createTask(task1));
        verify(userRepository).findUserByEmail(user1.getEmail());
        verify(userRepository).findById(user1.getId());
        verify(taskRepository).save(task1);
    }

    @Test
    public void testDeleteTask_taskNotExists() {
        when(taskRepository.findById(task1.getId())).thenReturn(Optional.empty());
        HttpStatus status = HttpStatus.NOT_FOUND;
        String error = "NOT_FOUND";
        String message = "This Task not exist";
        String path = "/task/{id}";
        assertEquals(new ResponseEntity(new ApiError(localDateTime
                , status
                , error
                , message
                , path), status), taskServiceImp.deleteTask(task1.getId()));
        verify(taskRepository).findById(task1.getId());
    }

    @Test
    public void testDeleteTask_notAuthorized() {
        task1.setUser(user1);
        when(taskRepository.findById(task1.getId())).thenReturn(Optional.of(task1));
        when(userRepository.findUserByEmail(user2.getEmail())).thenReturn(Optional.of(user2));
        when(userRepository.findById(task1.getUser().getId())).thenReturn(Optional.of(user1));
        TOKEN = jwtUtils.generateToken(new MyUserDetails(user2));
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String error = "UNAUTHORIZED";
        String message = "This Task not belong to this user";
        String path = "/task/{id}";
        assertEquals(new ResponseEntity(new ApiError(localDateTime
                , status
                , error
                , message
                , path), status), taskServiceImp.deleteTask(task1.getId()));
        verify(taskRepository).findById(task1.getId());
        verify(userRepository).findUserByEmail(user2.getEmail());
        verify(userRepository).findById(task1.getUser().getId());
    }

    @Test
    public void testDeleteTask() {
        //Everything alright
        task1.setUser(user1);
        when(taskRepository.findById(task1.getId())).thenReturn(Optional.of(task1));
        when(userRepository.findUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(userRepository.findById(task1.getUser().getId())).thenReturn(Optional.of(user1));
        TOKEN = jwtUtils.generateToken(new MyUserDetails(user1));
        assertEquals(new ResponseEntity(Optional.of(task1) + "\nRecord has been deleted", HttpStatus.OK)
                , taskServiceImp.deleteTask(task1.getId()));
        verify(taskRepository).deleteById(task1.getId());
        verify(taskRepository).findById(task1.getId());
        verify(userRepository).findUserByEmail(user1.getEmail());
        verify(userRepository).findById(task1.getUser().getId());
    }

    @Test
    public void testUpdateTask_taskNotExists() {
        when(taskRepository.findById(task1.getId())).thenReturn(Optional.empty());
        HttpStatus status = HttpStatus.NOT_FOUND;
        String error = "Not found";
        String message = "This user is not available";
        String path = "/task/{id}";

        assertEquals(new ResponseEntity(new ApiError(localDateTime
                , status
                , error
                , message
                , path), status), taskServiceImp.updateTask(task2, task1.getId()));
        verify(taskRepository).findById(task1.getId());
    }

    @Test
    public void testUpdateTask_notAuthorized() {
        task1.setUser(user1);
        System.out.println("user1 =   " + user1);
        when(taskRepository.findById(task1.getId())).thenReturn(Optional.of(task1));
        TOKEN = jwtUtils.generateToken(new MyUserDetails(user2));
        when(userRepository.findUserByEmail(user2.getEmail())).thenReturn(Optional.of(user2));
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String error = "UNAUTHORIZED";
        String message = "This Task not belongs to this user";
        String path = "/task/{id}";
        assertEquals(new ResponseEntity(new ApiError(localDateTime
                , status
                , error
                , message
                , path), status), taskServiceImp.updateTask(task2, task1.getId()));
        verify(taskRepository).findById(task1.getId());
        verify(userRepository).findUserByEmail(user2.getEmail());

    }

    @Test
    public void updateTaskTest() {
        //Everything alright
        task1.setUser(user1);
        when(taskRepository.findById(task1.getId())).thenReturn(Optional.of(task1));
        TOKEN = jwtUtils.generateToken(new MyUserDetails(user1));
        when(userRepository.findUserByEmail(task1.getUser().getEmail())).thenReturn(Optional.of(user1));
        when(taskRepository.save(task1)).thenReturn(task2);
        task2.setId(task1.getId());
        task2.setUser(task1.getUser());
        System.out.println(task1);
        System.out.println(task2);
        assertEquals(new ResponseEntity(Optional.of(task2), HttpStatus.OK)
                , taskServiceImp.updateTask(task2, task1.getId()));
        verify(taskRepository).findById(task1.getId());
        verify(userRepository).findUserByEmail(user1.getEmail());
        verify(taskRepository).save(task1);
    }

    @Test
    public void testGetTasks_noTasks() {
        TOKEN = jwtUtils.generateToken(new MyUserDetails(user1));
        when(userRepository.findUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(taskRepository.count()).thenReturn(0L);
        String error = "Empty";
        String message = "Nothing to present";
        String path = "/user";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        assertEquals(new ResponseEntity(new ApiError(localDateTime
                , status
                , error
                , message
                , path), status), taskServiceImp.getTasks());
        verify(userRepository).findUserByEmail(user1.getEmail());
        verify(taskRepository).count();
    }

    @Test
    public void testGetTasks() {
        //Everything alright
        TOKEN = jwtUtils.generateToken(new MyUserDetails(user1));
        user1.setTasks(List.of(task1, task2));
        when(userRepository.findUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(taskRepository.count()).thenReturn(1L);
        when(taskRepository.findTasksByUserId(user1.getId())).thenReturn(user1.getTasks());
        assertEquals(new ResponseEntity(user1.getTasks(), HttpStatus.OK), taskServiceImp.getTasks());
        verify(userRepository).findUserByEmail(user1.getEmail());
        verify(taskRepository).count();
        verify(taskRepository).findTasksByUserId(user1.getId());

    }

}