package com.example.taskmanager.security;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthenticationResponseModel implements Serializable {

    private String token;

    public AuthenticationResponseModel(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
