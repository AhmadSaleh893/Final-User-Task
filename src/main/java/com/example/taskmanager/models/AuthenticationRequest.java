package com.example.taskmanager.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AuthenticationRequest {
    private String username;
    private String password;
}
