package com.example.taskmanager.service;

import com.example.taskmanager.ApiError;
import com.example.taskmanager.repository.Task;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.taskmanager.security.TOKEN_GETTER.TOKEN;
import static com.example.taskmanager.security.TOKEN_GETTER.localDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {JwtUtils.class, UserServiceImp.class})
@ExtendWith(SpringExtension.class)
public class UserServiceImpTest {

    private User user1 = new User(1L, "Ahmad", "Ahmad@hotmail", "2513", 1, "ROLE_USER", 21);
    private User user2 = new User(2L, "Samer", "Samer@hotmail", "1234", 1, "ROLE_USER", 25);
    private User newUser = new User(8L, "new", "newk", "new", 1, "ROLE_USER", 91);
    private static String saveToken;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserServiceImp userServiceImp;

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
    public void isFitBaseAttributesTest() {
        assertNotNull(user1.getEmail());
        assertNotNull(user1.getPassword());
    }

    @Test
    public void testDeleteUser_notExists() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.empty());
        assertEquals(new ResponseEntity<ApiError>(new ApiError(localDateTime,
                        HttpStatus.NOT_FOUND, "NOT EXIST",
                        "Sorry this id you entered is not exist",
                        "/user/{id}"), HttpStatus.NOT_FOUND)
                , userServiceImp.deleteUser(user1.getId()));

        verify(userRepository).findById(user1.getId());

    }

    @Test
    public void testDeleteUser_notAuthorized() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        TOKEN = jwtUtils.generateToken(new MyUserDetails(user2));

        assertEquals(new ResponseEntity<ApiError>(new ApiError(localDateTime,
                        HttpStatus.UNAUTHORIZED, "NOT AUTHORIZED",
                        "Sorry you just can delete your self",
                        "/user/{id}"), HttpStatus.UNAUTHORIZED)
                , userServiceImp.deleteUser(user1.getId()));

        verify(userRepository).findById(user1.getId());

    }

    @Test
    public void testDeleteUser_userWithTasks() {
        Task task = new Task(100L, "user1 desc", true);
        List<Task> tasks = user1.getTasks();
        tasks.add(task);
        user1.setTasks(tasks);
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        TOKEN = jwtUtils.generateToken(new MyUserDetails(user1));

        String error = "Error with task entity";
        String message = "Can't delete this user because it has tasks depends on it";
        String path = "/user/{id}";

        assertEquals(new ResponseEntity(new ApiError(localDateTime, HttpStatus.CONFLICT, error, message, path)
                , HttpStatus.CONFLICT), userServiceImp.deleteUser(user1.getId()));
        verify(userRepository).findById(user1.getId());
    }

    @Test
    public void testDeleteUser() {
        //Everything alright
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        TOKEN = jwtUtils.generateToken(new MyUserDetails(user1));

        assertEquals(new ResponseEntity(Optional.of(user1) + "\nThis record has deleted", HttpStatus.OK)
                , userServiceImp.deleteUser(user1.getId()));

        verify(userRepository).findById(user1.getId());
        verify(userRepository).deleteById(user1.getId());
    }

    @Test
    public void testUpdateUser_notExist() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userServiceImp.updateUser(user1.getId(), user2);
        });
        verify(userRepository).findById(user1.getId());
        verify(userRepository, times(0)).save(user1);
    }

    @Test
    public void testUpdateUser_notAuthorized() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        TOKEN = jwtUtils.generateToken(new MyUserDetails(user2));

        assertEquals(new ResponseEntity<>(new ApiError(localDateTime,
                        HttpStatus.UNAUTHORIZED, "NOT AUTHORIZED",
                        "Sorry you just can Update your self",
                        "/user/{id}"), HttpStatus.UNAUTHORIZED)
                , userServiceImp.updateUser(user1.getId(), user2));

        verify(userRepository).findById(user1.getId());
        verify(userRepository, times(0)).save(user1);
    }

    @Test
    public void testUpdateUser_nullEmailOrPassword() {
        User user = new User();
        TOKEN = jwtUtils.generateToken(new MyUserDetails(user1));
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

        assertEquals(new ResponseEntity(new ApiError(localDateTime, HttpStatus.BAD_REQUEST
                , "Object name error", "wrong wording"
                , "/user/{id}"), HttpStatus.BAD_REQUEST), userServiceImp.updateUser(user1.getId(), user));
        verify(userRepository).findById(user1.getId());
        verify(userRepository, times(0)).save(user1);
        verify(userRepository, times(0)).findUserByEmail(newUser.getEmail());
    }

    @Test
    public void testUpdateUser_emptyEmailOrPassword() {
        User user = new User();
        user.setPassword("");
        user.setEmail("");
        TOKEN = jwtUtils.generateToken(new MyUserDetails(user1));
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        String message = "Email and password are required fields*";

        assertEquals(new ResponseEntity(new ApiError(localDateTime, HttpStatus.INTERNAL_SERVER_ERROR
                        , "NO NULL", message, "/user/{id}"), HttpStatus.INTERNAL_SERVER_ERROR)
                , userServiceImp.updateUser(user1.getId(), user));
        verify(userRepository).findById(user1.getId());
    }

    @Test
    public void testUpdateUser() {
//Everything alright
        TOKEN = jwtUtils.generateToken(new MyUserDetails(user1));
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        User newUser = new User(8L, "new", "newk", "new", 1, "ROLE_USER", 91);
        when(userRepository.save(user1)).thenReturn(newUser);
        when(passwordEncoder.encode(newUser.getPassword())).thenReturn(newUser.getPassword());
        newUser.setId(user1.getId());
        assertEquals(new ResponseEntity(Optional.of(newUser), HttpStatus.OK), userServiceImp.updateUser(user1.getId(), newUser));
        verify(userRepository).findById(user1.getId());
        verify(userRepository).save(user1);
        verify(userRepository).findUserByEmail(newUser.getEmail());
    }

}