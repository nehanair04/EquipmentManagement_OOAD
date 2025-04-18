package com.arenaedge.util;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.store.FolderException;
import javax.mail.internet.MimeMessage;

public class GreenMailManager {
    private static GreenMailManager instance;
    private GreenMail greenMail;
    private boolean isRunning = false;
    
    // Private constructor for singleton
    private GreenMailManager() {
        // Setup SMTP server on port 3025
        ServerSetup setup = new ServerSetup(3025, "localhost", ServerSetup.PROTOCOL_SMTP);
        greenMail = new GreenMail(setup);
    }
    
    // Singleton pattern
    public static synchronized GreenMailManager getInstance() {
        if (instance == null) {
            instance = new GreenMailManager();
        }
        return instance;
    }
    
    // Start the GreenMail server
    public void start() {
        if (!isRunning) {
            System.out.println("Starting GreenMail server...");
            greenMail.start();
            
            // Create test user
            greenMail.setUser("test@arenaedge.com", "test", "test");
            
            isRunning = true;
            System.out.println("GreenMail server started on port 3025");
        }
    }
    
    // Stop the GreenMail server
    public void stop() {
        if (isRunning) {
            System.out.println("Stopping GreenMail server...");
            greenMail.stop();
            isRunning = false;
            System.out.println("GreenMail server stopped");
        }
    }
    
    // Get received messages
    public MimeMessage[] getReceivedMessages() {
        return greenMail.getReceivedMessages();
    }
    
    // Clear received messages
    public void clearReceivedMessages() {
        try {
            greenMail.purgeEmailFromAllMailboxes();
        } catch (FolderException e) {
            System.err.println("Error purging emails: " + e.getMessage());
            e.printStackTrace();
        }
    }
}