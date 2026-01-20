package com.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users") // Renamed table to avoid SQL conflict
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    // Constructors
    public UserEntity() {}
    
    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}