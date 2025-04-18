package com.arenaedge.view.gym;

import com.arenaedge.controller.GymController;
import com.arenaedge.model.gym.GymLog;
import com.arenaedge.model.user.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Main panel for gym entry/exit logging.
 * Provides UI for recording entries and exits and showing current gym users.
 */
public class GymLogPanel extends JPanel {
    private JTextField userIdField;
    private JButton entryButton;
    private JButton exitButton;
    private JButton undoButton;
    private JTable currentUsersTable;
    private DefaultTableModel tableModel;
    
    private GymController controller;
    
    /**
     * Constructor for GymLogPanel
     */
    public GymLogPanel() {
        controller = new GymController();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Initialize components
        initializeComponents();
        
        // Add action listeners
        setupActionListeners();
        
        // Initial data load
        refreshCurrentUsers();
    }
    
    /**
     * Initialize UI components
     */
    private void initializeComponents() {
        // Top panel with control buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel userIdLabel = new JLabel("User ID:");
        userIdField = new JTextField(10);
        entryButton = new JButton("Record Entry");
        exitButton = new JButton("Record Exit");
        undoButton = new JButton("Undo Last");
        
        topPanel.add(userIdLabel);
        topPanel.add(userIdField);
        topPanel.add(entryButton);
        topPanel.add(exitButton);
        topPanel.add(undoButton);
        
        // Center panel with table of current users
        String[] columns = {"User ID", "Name", "Entry Time"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        currentUsersTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(currentUsersTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Users in Gym"));
        
        // Add panels to main panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Set up action listeners for buttons
     */
    private void setupActionListeners() {
        entryButton.addActionListener(e -> handleEntry());
        exitButton.addActionListener(e -> handleExit());
        undoButton.addActionListener(e -> handleUndo());
    }
    
    /**
     * Handle entry button click
     */
    private void handleEntry() {
        try {
            int userId = Integer.parseInt(userIdField.getText().trim());
            
            // Check if user can enter before proceeding
            if (!controller.canUserEnter(userId)) {
                JOptionPane.showMessageDialog(this, 
                    "User ID: " + userId + " is already in the gym and cannot enter again.", 
                    "Entry Not Allowed", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // In a real app, we would fetch the user from a service or DAO
            User user = new User();
            user.setUserId(userId);
            user.setUsername("user" + userId); // Default username for testing
            user.setName("User " + userId); // This would come from the database
            
            GymLog log = controller.recordEntry(user);
            
            if (log != null) {
                JOptionPane.showMessageDialog(this, 
                    "Entry recorded for User ID: " + userId, 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                userIdField.setText("");
                refreshCurrentUsers();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid User ID", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error recording entry: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleExit() {
        try {
            int userId = Integer.parseInt(userIdField.getText().trim());
            
            // Check if user can exit before proceeding
            if (!controller.canUserExit(userId)) {
                JOptionPane.showMessageDialog(this, 
                    "User ID: " + userId + " is not currently in the gym and cannot exit.", 
                    "Exit Not Allowed", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // In a real app, we would fetch the user from a service or DAO
            User user = new User();
            user.setUserId(userId);
            user.setUsername("user" + userId); // Default username for testing
            user.setName("User " + userId); // This would come from the database
            
            GymLog log = controller.recordExit(user);
            
            if (log != null) {
                JOptionPane.showMessageDialog(this, 
                    "Exit recorded for User ID: " + userId, 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                userIdField.setText("");
                refreshCurrentUsers();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid User ID", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error recording exit: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handle undo button click
     */
    private void handleUndo() {
        try {
            controller.undoLastOperation();
            JOptionPane.showMessageDialog(this, 
                "Last operation undone successfully", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshCurrentUsers();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error undoing operation: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Refresh the table of current gym users
     */
    private void refreshCurrentUsers() {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get current users from controller
        Map<Integer, GymLog> currentUsers = controller.getCurrentUsers();
        
        // Add to table
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        for (Map.Entry<Integer, GymLog> entry : currentUsers.entrySet()) {
            int userId = entry.getKey();
            GymLog log = entry.getValue();
            
            // In a real app, we would fetch the user name from a service
            String userName = "User " + userId; // This would come from the database
            
            Object[] row = {
                userId,
                userName,
                log.getTimestamp().format(formatter)
            };
            
            tableModel.addRow(row);
        }
    }
}