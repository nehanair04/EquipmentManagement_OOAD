package com.arenaedge.model.booking;

public class Court {
    private int id;
    private String name;
    private String type; // "BADMINTON", "SQUASH", etc.
    private boolean available;
    
    public Court() {
        this.available = true;
    }
    
    public Court(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.available = true;
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
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    @Override
    public String toString() {
        return name + " (" + type + ") - " + (available ? "Available" : "Unavailable");
    }
}