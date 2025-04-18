package com.arenaedge.command.gym;

import com.arenaedge.dao.GymLogDAO;
import com.arenaedge.model.gym.GymLog;
import com.arenaedge.model.gym.LogType;
import com.arenaedge.model.user.User;

import java.time.LocalDateTime;

/**
 * Command for recording a gym exit.
 * Part of the Command Pattern implementation.
 */
public class GymExitCommand implements GymCommand {
    private User user;
    private GymLogDAO gymLogDAO;
    private GymLog createdLog;
    
    /**
     * Constructor for GymExitCommand
     * 
     * @param user the user exiting the gym
     * @param gymLogDAO the data access object for gym logs
     */
    public GymExitCommand(User user, GymLogDAO gymLogDAO) {
        this.user = user;
        this.gymLogDAO = gymLogDAO;
    }
    
    @Override
    public void execute() {
        // Create a new exit log
        createdLog = new GymLog(user.getUserId(), LocalDateTime.now(), LogType.EXIT);
        
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