package com.arenaedge.command.gym;

/**
 * Command interface for gym operations.
 * Part of the Command Pattern implementation.
 */
public interface GymCommand {
    /**
     * Execute the command
     */
    void execute();
    
    /**
     * Undo the command
     */
    void undo();
}