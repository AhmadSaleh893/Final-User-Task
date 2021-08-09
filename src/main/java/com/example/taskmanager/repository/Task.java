package com.example.taskmanager.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(value={"user"})
@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor

public class Task{
    @Id
    @GeneratedValue
    private Long id;

    private String description;

    @NonNull
    private Boolean completed;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    public Task(Long id
            , String description
            , @NonNull Boolean completed) {
        this.id = id;
        this.description = description;
        this.completed = completed;

    }
}
