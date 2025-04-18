package com.arenaedge.command.gym;

import java.util.ArrayList;
import java.util.List;

/**
 * Command invoker for gym access operations.
 * Part of the Command Pattern implementation.
 */
public class GymAccessController {
    private List<GymCommand> commandHistory = new ArrayList<>();
    
    /**
     * Process a command
     * 
     * @param command the command to process
     */
    public void processCommand(GymCommand command) {
        // Execute the command
        command.execute();
        
        // Add to history for potential undo operations
        commandHistory.add(command);
    }
    
    /**
     * Undo the last command
     */
    public void undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            GymCommand lastCommand = commandHistory.remove(commandHistory.size() - 1);
            lastCommand.undo();
        }
    }
    
    /**
     * Get the history of commands
     * 
     * @return list of executed commands
     */
    public List<GymCommand> getCommandHistory() {
        return new ArrayList<>(commandHistory);
    }
}