
package com.example.taskmanager.service;

import com.example.taskmanager.repository.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.AuthenticationRequestModel;
import com.example.taskmanager.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public MyUserDetailsService(UserRepository userRepository
            , PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        Optional<User> user = userRepository.findUserByEmail(email);
//        user.orElseThrow(() -> new UsernameNotFoundException("Email not found: " + email));
//        return user.map(MyUserDetails::new).get();

        //or

        Optional<User> user = userRepository.findUserByEmail(email);
        if (user.isPresent() == false) {
            throw new UsernameNotFoundException("Email Not Found" + email);
        } else {
            MyUserDetails myUserDetails = new MyUserDetails(user.get());
            return myUserDetails;
        }
    }

    public User createUser(AuthenticationRequestModel model) {

        User user = new User(model.getName()
                , model.getEmail()
                , passwordEncoder.encode(model.getPassword())
                , model.getAge()
                , "ROLE_USER"
                , 1);

        userRepository.save(user);
        return user;
    }

}