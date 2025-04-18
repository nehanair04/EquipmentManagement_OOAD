package com.arenaedge.model.booking;

import java.sql.Timestamp;

public class Booking {
    private int id;
    private int userId;
    private int courtId;
    private Timestamp startTime;
    private Timestamp endTime;
    private String status; // "SCHEDULED", "CANCELLED", "COMPLETED"
    private String courtType; // "BADMINTON", "SQUASH", etc.
    
    public Booking() {
        this.status = "SCHEDULED";
    }
    
    public Booking(int userId, int courtId, Timestamp startTime, Timestamp endTime, String courtType) {
        this.userId = userId;
        this.courtId = courtId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.courtType = courtType;
        this.status = "SCHEDULED";
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getCourtId() {
        return courtId;
    }
    
    public void setCourtId(int courtId) {
        this.courtId = courtId;
    }
    
    public Timestamp getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }
    
    public Timestamp getEndTime() {
        return endTime;
    }
    
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCourtType() {
        return courtType;
    }
    
    public void setCourtType(String courtType) {
        this.courtType = courtType;
    }
    
    @Override
    public String toString() {
        return "Booking #" + id + ": " + courtType + " Court #" + courtId + 
               " (" + startTime + " to " + endTime + ") - " + status;
    }
}