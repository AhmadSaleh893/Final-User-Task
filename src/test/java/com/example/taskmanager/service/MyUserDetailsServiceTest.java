package com.example.taskmanager.service;

import com.example.taskmanager.repository.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.AuthenticationRequestModel;
import com.example.taskmanager.security.MyUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {MyUserDetailsService.class})
@ExtendWith(SpringExtension.class)
public class MyUserDetailsServiceTest {

    private User user1 = new User(1L, "Ahmad", "Ahmad@hotmail", "2513", 1, "ROLE_USER", 21);

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void testLoadUserByUsername_userExist() {
        when(userRepository.findUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        assertEquals(new MyUserDetails(user1)
                , myUserDetailsService.loadUserByUsername(user1.getEmail()));
        verify(userRepository).findUserByEmail(user1.getEmail());
    }

    @Test
    public void testLoadUserByUsername_userNotExists() {
        when(userRepository.findUserByEmail(user1.getEmail())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class
                , () -> myUserDetailsService.loadUserByUsername(user1.getEmail()));
        verify(userRepository).findUserByEmail(user1.getEmail());
    }

    @Test
    public void createUserTest() {
        //Everything alright
        user1.setId(null);
        AuthenticationRequestModel model = new AuthenticationRequestModel(user1.getName(), user1.getEmail(), user1.getPassword(), user1.getAge());
        when(userRepository.save(user1)).thenReturn(user1);
        when(passwordEncoder.encode((CharSequence) any())).thenReturn("foo");
        user1.setPassword("foo");
        assertEquals(user1, myUserDetailsService.createUser(model));

    }

}

