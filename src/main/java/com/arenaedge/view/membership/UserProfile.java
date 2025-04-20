package com.arenaedge.view.membership;

import com.arenaedge.controller.UserProfileController;
import com.arenaedge.model.user.User;
import com.arenaedge.model.user.UserNotificationSettings;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for user profile customization.
 */
public class UserProfilePanel extends JPanel {
    private UserProfileController controller;
    
    // User info components
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JButton updateUserBtn;
    private JButton changePasswordBtn;
    
    // Notification settings components
    private JCheckBox emailNotificationsCb;
    private JCheckBox smsNotificationsCb;
    private JCheckBox bookingRemindersCb;
    private JCheckBox paymentRemindersCb;
    private JCheckBox promotionalNotificationsCb;
    private JComboBox<Integer> reminderHoursCombo;
    private JButton saveSettingsBtn;
    
    // Current user (hardcoded for demo purposes)
    private User currentUser;
    private UserNotificationSettings notificationSettings;
    
    /**
     * Constructor for UserProfilePanel
     */
    public UserProfilePanel() {
        this.controller = new UserProfileController();
        
        setLayout(new BorderLayout());
        
        // Initialize with a hardcoded user ID for demo
        loadUserData(1);
        
        // Create main components
        createUserInfoPanel();
        createNotificationSettingsPanel();
    }
    
    /**
     * Load user data from database
     * 
     * @param userId the user ID to load
     */
    private void loadUserData(int userId) {
        currentUser = controller.getUserById(userId);
        notificationSettings = controller.getNotificationSettings(userId);
    }
    
    /**
     * Create the user info panel
     */
    private void createUserInfoPanel() {
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBorder(BorderFactory.createTitledBorder("User Information"));
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        // Name field
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(currentUser.getName());
        
        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(currentUser.getEmail());
        
        // Phone field
        JLabel phoneLabel = new JLabel("Phone:");
        phoneField = new JTextField(currentUser.getPhone());
        
        // Add to form panel
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(phoneLabel);
        formPanel.add(phoneField);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        updateUserBtn = new JButton("Update Information");
        changePasswordBtn = new JButton("Change Password");
        
        // Add action listeners
        updateUserBtn.addActionListener(e -> handleUpdateUser());
        changePasswordBtn.addActionListener(e -> handleChangePassword());
        
        // Add to button panel
        buttonPanel.add(updateUserBtn);
        buttonPanel.add(changePasswordBtn);
        
        // Add components to user panel
        userPanel.add(formPanel, BorderLayout.CENTER);
        userPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add to main panel
        add(userPanel, BorderLayout.NORTH);
    }
    
    /**
     * Create the notification settings panel
     */
    private void createNotificationSettingsPanel() {
        JPanel settingsPanel = new JPanel(new BorderLayout());
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Notification Settings"));
        
        // Create checkboxes panel
        JPanel checkboxesPanel = new JPanel(new GridLayout(5, 1));
        
        emailNotificationsCb = new JCheckBox("Email Notifications", notificationSettings.isEmailNotifications());
        smsNotificationsCb = new JCheckBox("SMS Notifications", notificationSettings.isSmsNotifications());
        bookingRemindersCb = new JCheckBox("Booking Reminders", notificationSettings.isBookingReminders());
        paymentRemindersCb = new JCheckBox("Payment Reminders", notificationSettings.isPaymentReminders());
        promotionalNotificationsCb = new JCheckBox("Promotional Notifications", notificationSettings.isPromotionalNotifications());
        
        // Add to checkboxes panel
        checkboxesPanel.add(emailNotificationsCb);
        checkboxesPanel.add(smsNotificationsCb);
        checkboxesPanel.add(bookingRemindersCb);
        checkboxesPanel.add(paymentRemindersCb);
        checkboxesPanel.add(promotionalNotificationsCb);
        
        // Create reminder hours panel
        JPanel reminderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel reminderLabel = new JLabel("Send reminders this many hours before events:");
        Integer[] hourOptions = {1, 2, 4, 8, 12, 24, 48, 72};
        reminderHoursCombo = new JComboBox<>(hourOptions);
        
        // Set selected hours
        for (int i = 0; i < hourOptions.length; i++) {
            if (hourOptions[i] == notificationSettings.getReminderHours()) {
                reminderHoursCombo.setSelectedIndex(i);
                break;
            }
        }
        
        reminderPanel.add(reminderLabel);
        reminderPanel.add(reminderHoursCombo);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        saveSettingsBtn = new JButton("Save Settings");
        JButton enableAllBtn = new JButton("Enable All");
        JButton disableAllBtn = new JButton("Disable All");
        
        // Add action listeners
        saveSettingsBtn.addActionListener(e -> handleSaveSettings());
        enableAllBtn.addActionListener(e -> {
            emailNotificationsCb.setSelected(true);
            smsNotificationsCb.setSelected(true);
            bookingRemindersCb.setSelected(true);
            paymentRemindersCb.setSelected(true);
            promotionalNotificationsCb.setSelected(true);
        });
        
        disableAllBtn.addActionListener(e -> {
            emailNotificationsCb.setSelected(false);
            smsNotificationsCb.setSelected(false);
            bookingRemindersCb.setSelected(false);
            paymentRemindersCb.setSelected(false);
            promotionalNotificationsCb.setSelected(false);
        });
        
        // Add to button panel
        buttonPanel.add(enableAllBtn);
        buttonPanel.add(disableAllBtn);
        buttonPanel.add(saveSettingsBtn);
        
        // Create a panel to hold both reminder and button panels
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(reminderPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add components to settings panel
        settingsPanel.add(checkboxesPanel, BorderLayout.CENTER);
        settingsPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Add to main panel
        add(settingsPanel, BorderLayout.CENTER);
    }
    
    /**
     * Handle updating user information
     */
    private void handleUpdateUser() {
        // Update user object with form values
        currentUser.setName(nameField.getText());
        currentUser.setEmail(emailField.getText());
        currentUser.setPhone(phoneField.getText());
        
        // Save to database
        boolean success = controller.updateUser(currentUser);
        
        if (success) {
            JOptionPane.showMessageDialog(
                this,
                "User information updated successfully",
                "Update Successful",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to update user information",
                "Update Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Handle changing password
     */
    private void handleChangePassword() {
        // Show password change dialog
        JPanel passwordPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        
        JLabel oldPassLabel = new JLabel("Current Password:");
        JPasswordField oldPassField = new JPasswordField(15);
        
        JLabel newPassLabel = new JLabel("New Password:");
        JPasswordField newPassField = new JPasswordField(15);
        
        JLabel confirmPassLabel = new JLabel("Confirm New Password:");
        JPasswordField confirmPassField = new JPasswordField(15);
        
        passwordPanel.add(oldPassLabel);
        passwordPanel.add(oldPassField);
        passwordPanel.add(newPassLabel);
        passwordPanel.add(newPassField);
        passwordPanel.add(confirmPassLabel);
        passwordPanel.add(confirmPassField);
        
        int option = JOptionPane.showConfirmDialog(
            this,
            passwordPanel,
            "Change Password",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (option == JOptionPane.OK_OPTION) {
            String oldPass = new String(oldPassField.getPassword());
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());
            
            // Validate passwords
            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Passwords cannot be empty",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(
                    this,
                    "New passwords do not match",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            // Call controller to change password
            boolean success = controller.changePassword(currentUser.getUserId(), oldPass, newPass);
            
            if (success) {
                JOptionPane.showMessageDialog(
                    this,
                    "Password changed successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to change password. Please check your current password.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    /**
     * Handle saving notification settings
     */
    private void handleSaveSettings() {
        // Update notification settings object with form values
        notificationSettings.setEmailNotifications(emailNotificationsCb.isSelected());
        notificationSettings.setSmsNotifications(smsNotificationsCb.isSelected());
        notificationSettings.setBookingReminders(bookingRemindersCb.isSelected());
        notificationSettings.setPaymentReminders(paymentRemindersCb.isSelected());
        notificationSettings.setPromotionalNotifications(promotionalNotificationsCb.isSelected());
        notificationSettings.setReminderHours((Integer) reminderHoursCombo.getSelectedItem());
        
        // Save to database
        boolean success = controller.saveNotificationSettings(notificationSettings);
        
        if (success) {
            JOptionPane.showMessageDialog(
                this,
                "Notification settings saved successfully",
                "Save Successful",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to save notification settings",
                "Save Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
