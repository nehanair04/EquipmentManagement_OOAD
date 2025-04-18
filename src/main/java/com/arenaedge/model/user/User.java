package com.arenaedge.model.user;

import java.time.LocalDateTime;

/**
 * Represents a user in the system.
 * This class serves as a common model used across all modules.
 */
public class User {
    private int userId;
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    
    /**
     * Default constructor
     */
    public User() {
    }
    
    /**
     * Constructor with essential fields
     * 
     * @param userId the unique identifier for the user
     * @param username the user's username
     * @param name the user's name
     */
    public User(int userId, String username, String name) {
        this.userId = userId;
        this.username = username;
        this.name = name;
    }
    
    /**
     * Full constructor
     * 
     * @param userId the unique identifier for the user
     * @param username the user's username
     * @param password the user's password (hashed)
     * @param name the user's name
     * @param email the user's email address
     * @param phone the user's phone number
     * @param createdAt when the user was created
     */
    public User(int userId, String username, String password, String name, String email, String phone, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        User user = (User) o;
        
        return userId == user.userId;
    }
    
    @Override
    public int hashCode() {
        return userId;
    }
}