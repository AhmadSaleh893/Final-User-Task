package com.example.taskmanager.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(value={"tasks"})
@Entity
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    @NonNull
    private String email;

    @NonNull
    private String password;

    private String name;

    private Integer active;

    @NonNull
    private String roles;

    private int age;

    @OneToMany(mappedBy = "user")
    private List<Task> tasks = new ArrayList<>();

    public User(Long id
            , String name
            , @NonNull String email
            , @NonNull String password
            , Integer active
            , String roles
            , int age) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.active = active;
        this.roles = roles;
        this.age = age;
    }

    public User(String name
            , @NonNull String email
            , @NonNull String password
            , int age
            , String roles
            , Integer active) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.active = active;
        this.roles = roles;
    }

    public User(@NonNull String email
            , @NonNull String password
            , String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}