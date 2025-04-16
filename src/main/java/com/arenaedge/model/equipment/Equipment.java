package com.arenaedge.model.equipment;

import java.sql.Timestamp;

// Base class implementing Single Responsibility Principle
public abstract class Equipment {
    private int id;
    private String name;
    private String type;
    private String status;
    private int conditionRating;
    private Timestamp createdAt;
    private Timestamp lastCheckedOut;
    
    public Equipment() {
        this.status = "AVAILABLE";
        this.conditionRating = 5;
    }
    
    public Equipment(int id, String name, String type, String status, int conditionRating) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.conditionRating = conditionRating;
    }
    
    // Equipment-specific validation method
    public abstract boolean validateEquipment();
    
    // Equipment-specific maintenance requirements
    public abstract String getMaintenanceRequirements();
    
    // Common methods for all equipment
    public void checkOut() {
        if ("AVAILABLE".equals(this.status)) {
            this.status = "CHECKED_OUT";
            this.lastCheckedOut = new Timestamp(System.currentTimeMillis());
        } else {
            throw new IllegalStateException("Equipment is not available for checkout");
        }
    }
    
    public void returnEquipment() {
        if ("CHECKED_OUT".equals(this.status)) {
            this.status = "AVAILABLE";
        } else {
            throw new IllegalStateException("Equipment was not checked out");
        }
    }
    
    public void sendForMaintenance() {
        this.status = "MAINTENANCE";
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public int getConditionRating() {
        return conditionRating;
    }
    
    public void setConditionRating(int conditionRating) {
        this.conditionRating = conditionRating;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getLastCheckedOut() {
        return lastCheckedOut;
    }
    
    public void setLastCheckedOut(Timestamp lastCheckedOut) {
        this.lastCheckedOut = lastCheckedOut;
    }
    
    @Override
    public String toString() {
        return name + " (" + type + ") - " + status;
    }
}