package com.arenaedge.controller;

import com.arenaedge.command.gym.GymAccessController;
import com.arenaedge.command.gym.GymEntryCommand;
import com.arenaedge.command.gym.GymExitCommand;
import com.arenaedge.dao.GymLogDAO;
import com.arenaedge.dao.impl.GymLogDAOImpl;
import com.arenaedge.model.gym.GymLog;
import com.arenaedge.model.gym.LogType;
import com.arenaedge.model.user.User;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller for gym access operations.
 * Applies the Low Coupling principle by centralizing interactions between UI and data.
 */
public class GymController {
    private GymLogDAO gymLogDAO;
    private GymAccessController accessController;
    
    /**
     * Constructor for GymController
     */
    public GymController() {
        this.gymLogDAO = new GymLogDAOImpl();
        this.accessController = new GymAccessController();
    }
    
    /**
     * Checks if a user can enter the gym (if they're already out or have no records)
     * 
     * @param userId the user ID to check
     * @return true if the user can enter, false otherwise
     */
    public boolean canUserEnter(int userId) {
        // Get user's latest log entry
        List<GymLog> userLogs = gymLogDAO.getLogsByUser(userId);
        
        // If no logs, user can enter
        if (userLogs.isEmpty()) {
            return true;
        }
        
        // Sort logs by timestamp (newest first)
        userLogs.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        
        // Check if the most recent log is an EXIT
        return userLogs.get(0).getType() == LogType.EXIT;
    }
    
    /**
     * Checks if a user can exit the gym (if they're already in)
     * 
     * @param userId the user ID to check
     * @return true if the user can exit, false otherwise
     */
    public boolean canUserExit(int userId) {
        // Get user's latest log entry
        List<GymLog> userLogs = gymLogDAO.getLogsByUser(userId);
        
        // If no logs, user can't exit
        if (userLogs.isEmpty()) {
            return false;
        }
        
        // Sort logs by timestamp (newest first)
        userLogs.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        
        // Check if the most recent log is an ENTRY
        return userLogs.get(0).getType() == LogType.ENTRY;
    }
    
    /**
     * Record a user's entry to the gym if allowed
     * 
     * @param user the user entering
     * @return the created log or null if not allowed
     */
    public GymLog recordEntry(User user) {
        if (!canUserEnter(user.getUserId())) {
            return null; // User not allowed to enter
        }
        
        GymEntryCommand entryCommand = new GymEntryCommand(user, gymLogDAO);
        accessController.processCommand(entryCommand);
        return entryCommand.getCreatedLog();
    }
    
    /**
     * Record a user's exit from the gym if allowed
     * 
     * @param user the user exiting
     * @return the created log or null if not allowed
     */
    public GymLog recordExit(User user) {
        if (!canUserExit(user.getUserId())) {
            return null; // User not allowed to exit
        }
        
        GymExitCommand exitCommand = new GymExitCommand(user, gymLogDAO);
        accessController.processCommand(exitCommand);
        return exitCommand.getCreatedLog();
    }
    
    /**
     * Undo the last gym access operation
     */
    public void undoLastOperation() {
        accessController.undoLastCommand();
    }
    
    /**
     * Get users currently in the gym
     * 
     * @return map of userId to entry log
     */
    public Map<Integer, GymLog> getCurrentUsers() {
        Map<Integer, GymLog> currentUsers = new HashMap<>();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        
        // Get all logs for today
        List<GymLog> todaysLogs = gymLogDAO.getLogsByDateRange(startOfDay, LocalDateTime.now());
        
        // Process logs to find current users (those with entry but no exit)
        for (GymLog log : todaysLogs) {
            if (log.getType() == LogType.ENTRY) {
                currentUsers.put(log.getUserId(), log);
            } else if (log.getType() == LogType.EXIT) {
                currentUsers.remove(log.getUserId());
            }
        }
        
        return currentUsers;
    }
    
    /**
     * Get logs for a specific date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of logs in the range
     */
    public List<GymLog> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return gymLogDAO.getLogsByDateRange(startDate, endDate);
    }
    
    /**
     * Get all logs for a specific user
     * 
     * @param userId the user ID
     * @return list of logs for the user
     */
    public List<GymLog> getLogsByUser(int userId) {
        return gymLogDAO.getLogsByUser(userId);
    }
    
    /**
     * Calculate hourly usage data for a date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return map of hour to count of entries
     */
    public Map<Integer, Integer> calculateHourlyUsage(LocalDateTime startDate, LocalDateTime endDate) {
        List<GymLog> logs = gymLogDAO.getLogsByDateRange(startDate, endDate);
        
        // Initialize map with all hours
        Map<Integer, Integer> hourlyUsage = new HashMap<>();
        for (int i = 0; i < 24; i++) {
            hourlyUsage.put(i, 0);
        }
        
        // Count entries per hour
        for (GymLog log : logs) {
            if (log.getType() == LogType.ENTRY) {
                int hour = log.getTimestamp().getHour();
                hourlyUsage.put(hour, hourlyUsage.getOrDefault(hour, 0) + 1);
            }
        }
        
        return hourlyUsage;
    }
    
    /**
     * Calculate average gym time for all users in a date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return average time in minutes
     */
    public long calculateAverageGymTime(LocalDateTime startDate, LocalDateTime endDate) {
        List<GymLog> logs = gymLogDAO.getLogsByDateRange(startDate, endDate);
        
        // Group logs by user
        Map<Integer, List<GymLog>> userLogs = new HashMap<>();
        for (GymLog log : logs) {
            if (!userLogs.containsKey(log.getUserId())) {
                userLogs.put(log.getUserId(), new ArrayList<>());
            }
            userLogs.get(log.getUserId()).add(log);
        }
        
        long totalMinutes = 0;
        int sessionsCount = 0;
        
        // Calculate duration for each user session
        for (List<GymLog> userLogList : userLogs.values()) {
            // Sort logs by timestamp
            userLogList.sort((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()));
            
            // Match entry-exit pairs
            for (int i = 0; i < userLogList.size() - 1; i++) {
                GymLog currentLog = userLogList.get(i);
                GymLog nextLog = userLogList.get(i + 1);
                
                if (currentLog.getType() == LogType.ENTRY && nextLog.getType() == LogType.EXIT) {
                    Duration duration = Duration.between(currentLog.getTimestamp(), nextLog.getTimestamp());
                    totalMinutes += duration.toMinutes();
                    sessionsCount++;
                    i++; // Skip the exit log in the next iteration
                }
            }
        }
        
        return sessionsCount > 0 ? totalMinutes / sessionsCount : 0;
    }
}