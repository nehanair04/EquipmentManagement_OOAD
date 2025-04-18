package com.arenaedge.model.gym;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Represents a gym entry or exit event.
 * Part of the Command Pattern implementation where this acts as a receiver.
 */
public class GymLog {
    private int logId;
    private int userId;
    private LocalDateTime timestamp;
    private LogType type;
    
    /**
     * Default constructor
     */
    public GymLog() {
    }
    
    /**
     * Constructor with all fields except logId
     * 
     * @param userId the ID of the user
     * @param timestamp the time of the log event
     * @param type the type of log (ENTRY or EXIT)
     */
    public GymLog(int userId, LocalDateTime timestamp, LogType type) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.type = type;
    }

    /**
     * Calculate duration between this log and an entry log
     * Only valid for EXIT logs
     * 
     * @param entryLog the entry log to compare against
     * @return Duration between logs or null if invalid
     */
    public Duration calculateDuration(GymLog entryLog) {
        if (this.type == LogType.EXIT && entryLog.getType() == LogType.ENTRY) {
            return Duration.between(entryLog.getTimestamp(), this.timestamp);
        }
        return null;
    }

    // Getters and Setters
    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return "GymLog{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", timestamp=" + timestamp +
                ", type=" + type +
                '}';
    }
}