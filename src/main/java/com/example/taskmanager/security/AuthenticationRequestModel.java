package com.example.taskmanager.security;

import com.example.taskmanager.repository.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class AuthenticationRequestModel implements Serializable {

    private String name;
    private String email;
    private String password;
    private int age;

    //need default constructor for JSON Parsing
    public AuthenticationRequestModel() {

    }

    public AuthenticationRequestModel(String name
            , String email
            , String password
            , int age) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
    }

    public AuthenticationRequestModel(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.age = user.getAge();
    }
}
