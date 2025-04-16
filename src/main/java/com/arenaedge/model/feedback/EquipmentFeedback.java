package com.arenaedge.model.feedback;

import java.sql.Timestamp;

// SRP - This class is only responsible for feedback data
public class EquipmentFeedback {
    private int id;
    private int equipmentId;
    private int userId;
    private int rating;
    private String comments;
    private Timestamp createdAt;
    
    public EquipmentFeedback() {
    }
    
    public EquipmentFeedback(int equipmentId, int userId, int rating, String comments) {
        this.equipmentId = equipmentId;
        this.userId = userId;
        this.rating = rating;
        this.comments = comments;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getEquipmentId() {
        return equipmentId;
    }
    
    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }
    
    public String getComments() {
        return comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}