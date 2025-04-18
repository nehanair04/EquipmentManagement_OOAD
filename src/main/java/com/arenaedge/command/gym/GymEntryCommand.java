package com.arenaedge.command.gym;

import com.arenaedge.dao.GymLogDAO;
import com.arenaedge.model.gym.GymLog;
import com.arenaedge.model.gym.LogType;
import com.arenaedge.model.user.User;

import java.time.LocalDateTime;

/**
 * Command for recording a gym entry.
 * Part of the Command Pattern implementation.
 */
public class GymEntryCommand implements GymCommand {
    private User user;
    private GymLogDAO gymLogDAO;
    private GymLog createdLog;
    
    /**
     * Constructor for GymEntryCommand
     * 
     * @param user the user entering the gym
     * @param gymLogDAO the data access object for gym logs
     */
    public GymEntryCommand(User user, GymLogDAO gymLogDAO) {
        this.user = user;
        this.gymLogDAO = gymLogDAO;
    }
    
    @Override
    public void execute() {
        // Create a new entry log
        createdLog = new GymLog(user.getUserId(), LocalDateTime.now(), LogType.ENTRY);
        
        // Save to database
        gymLogDAO.saveLog(createdLog);
    }
    
    @Override
    public void undo() {
        // If a log was created, delete it
        if (createdLog != null && createdLog.getLogId() > 0) {
            gymLogDAO.deleteLog(createdLog.getLogId());
            createdLog = null;
        }
    }
    
    /**
     * Get the created log
     * 
     * @return the log created by this command
     */
    public GymLog getCreatedLog() {
        return createdLog;
    }
}