package com.example.taskmanager.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class AuthenticaionResponse {
    public final String jwt;
}
