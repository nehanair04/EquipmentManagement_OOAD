package com.arenaedge.dao;

import com.arenaedge.model.gym.GymLog;
import com.arenaedge.model.gym.LogType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface for GymLog data access operations.
 * Part of implementing the Low Coupling principle.
 */
public interface GymLogDAO {
    /**
     * Save a gym log to the database
     * 
     * @param log the log to save
     */
    void saveLog(GymLog log);
    
    /**
     * Delete a gym log by ID
     * 
     * @param logId the ID of the log to delete
     */
    void deleteLog(int logId);
    
    /**
     * Get a gym log by ID
     * 
     * @param logId the ID of the log to retrieve
     * @return the log or null if not found
     */
    GymLog getLogById(int logId);
    
    /**
     * Get all logs for a specific user
     * 
     * @param userId the user ID to filter by
     * @return list of logs for the user
     */
    List<GymLog> getLogsByUser(int userId);
    
    /**
     * Get logs by type (ENTRY or EXIT)
     * 
     * @param type the type to filter by
     * @return list of logs of the specified type
     */
    List<GymLog> getLogsByType(LogType type);
    
    /**
     * Get logs within a date range
     * 
     * @param start the start datetime
     * @param end the end datetime
     * @return list of logs within the range
     */
    List<GymLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end);
    
    /**
     * Get all entry logs that don't have a matching exit
     * 
     * @return list of unmatched entry logs
     */
    List<GymLog> getUnmatchedEntries();
}