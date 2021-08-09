package com.example.taskmanager;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public
class ApiError {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private HttpStatus status;
    private String error;
    private String message;
    private String path;

    public ApiError(String x)
    {
        message = x;
    }

    public ApiError(LocalDateTime localDateTime
            ,HttpStatus status
            ,String error
            , String message
            ,String path) {
        this.timestamp = localDateTime;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ApiError() {

    }
}
