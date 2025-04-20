package com.arenaedge.controller;

import com.arenaedge.model.user.User;
import com.arenaedge.model.user.UserNotificationSettings;
import com.arenaedge.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for user profile operations.
 * Handles user information and notification settings.
 */
public class UserProfileController {
    
    /**
     * Get a user by ID
     * 
     * @param userId the user ID
     * @return the user or null if not found
     */
    public User getUserById(int userId) {
        String query = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get a user by username
     * 
     * @param username the username
     * @return the user or null if not found
     */
    public User getUserByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Update user information
     * 
     * @param user the user to update
     * @return true if update was successful
     */
    public boolean updateUser(User user) {
        String query = "UPDATE users SET name = ?, email = ?, phone = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            stmt.setInt(4, user.getUserId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Change user password
     * 
     * @param userId the user ID
     * @param oldPassword the old password (for verification)
     * @param newPassword the new password
     * @return true if password change was successful
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        // First, verify the old password
        String verifyQuery = "SELECT password FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement verifyStmt = conn.prepareStatement(verifyQuery)) {
            
            verifyStmt.setInt(1, userId);
            
            try (ResultSet rs = verifyStmt.executeQuery()) {
                if (rs.next()) {
                    String currentPassword = rs.getString("password");
                    
                    // In a real app, we would use password hashing
                    if (!oldPassword.equals(currentPassword)) {
                        return false; // Old password doesn't match
                    }
                } else {
                    return false; // User not found
                }
            }
            
            // If verification passed, update the password
            String updateQuery = "UPDATE users SET password = ? WHERE user_id = ?";
            
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, newPassword); // In a real app, we would hash the password
                updateStmt.setInt(2, userId);
                
                int affectedRows = updateStmt.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get notification settings for a user
     * 
     * @param userId the user ID
     * @return the notification settings
     */
    public UserNotificationSettings getNotificationSettings(int userId) {
        String query = "SELECT * FROM user_notification_settings WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNotificationSettings(rs);
                } else {
                    // If no settings exist, create default settings
                    UserNotificationSettings settings = new UserNotificationSettings(userId);
                    saveNotificationSettings(settings);
                    return settings;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return new UserNotificationSettings(userId);
    }
    
    /**
     * Save notification settings
     * 
     * @param settings the settings to save
     * @return true if save was successful
     */
    public boolean saveNotificationSettings(UserNotificationSettings settings) {
        // Check if settings already exist
        String checkQuery = "SELECT COUNT(*) FROM user_notification_settings WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            
            checkStmt.setInt(1, settings.getUserId());
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Update existing settings
                    String updateQuery = "UPDATE user_notification_settings SET " +
                                        "email_notifications = ?, sms_notifications = ?, " +
                                        "booking_reminders = ?, payment_reminders = ?, " +
                                        "promotional_notifications = ?, reminder_hours = ? " +
                                        "WHERE user_id = ?";
                    
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setBoolean(1, settings.isEmailNotifications());
                        updateStmt.setBoolean(2, settings.isSmsNotifications());
                        updateStmt.setBoolean(3, settings.isBookingReminders());
                        updateStmt.setBoolean(4, settings.isPaymentReminders());
                        updateStmt.setBoolean(5, settings.isPromotionalNotifications());
                        updateStmt.setInt(6, settings.getReminderHours());
                        updateStmt.setInt(7, settings.getUserId());
                        
                        int affectedRows = updateStmt.executeUpdate();
                        return affectedRows > 0;
                    }
                } else {
                    // Insert new settings
                    String insertQuery = "INSERT INTO user_notification_settings " +
                                        "(user_id, email_notifications, sms_notifications, " +
                                        "booking_reminders, payment_reminders, " +
                                        "promotional_notifications, reminder_hours) " +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, settings.getUserId());
                        insertStmt.setBoolean(2, settings.isEmailNotifications());
                        insertStmt.setBoolean(3, settings.isSmsNotifications());
                        insertStmt.setBoolean(4, settings.isBookingReminders());
                        insertStmt.setBoolean(5, settings.isPaymentReminders());
                        insertStmt.setBoolean(6, settings.isPromotionalNotifications());
                        insertStmt.setInt(7, settings.getReminderHours());
                        
                        int affectedRows = insertStmt.executeUpdate();
                        return affectedRows > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get all users from the database
     * 
     * @return list of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Map a ResultSet to a User object
     * 
     * @param rs the ResultSet
     * @return the User object
     * @throws SQLException if mapping fails
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        } else {
            user.setCreatedAt(LocalDateTime.now());
        }
        
        return user;
    }
    
    /**
     * Map a ResultSet to a UserNotificationSettings object
     * 
     * @param rs the ResultSet
     * @return the UserNotificationSettings object
     * @throws SQLException if mapping fails
     */
    private UserNotificationSettings mapResultSetToNotificationSettings(ResultSet rs) throws SQLException {
        UserNotificationSettings settings = new UserNotificationSettings();
        settings.setUserId(rs.getInt("user_id"));
        settings.setEmailNotifications(rs.getBoolean("email_notifications"));
        settings.setSmsNotifications(rs.getBoolean("sms_notifications"));
        settings.setBookingReminders(rs.getBoolean("booking_reminders"));
        settings.setPaymentReminders(rs.getBoolean("payment_reminders"));
        settings.setPromotionalNotifications(rs.getBoolean("promotional_notifications"));
        settings.setReminderHours(rs.getInt("reminder_hours"));
        return settings;
    }
}
