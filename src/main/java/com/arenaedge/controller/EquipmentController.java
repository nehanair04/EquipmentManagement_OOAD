package com.arenaedge.controller;

import com.arenaedge.model.equipment.Equipment;
import com.arenaedge.model.equipment.factory.EquipmentFactory;
import com.arenaedge.model.feedback.EquipmentFeedback;
import com.arenaedge.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// SRP - Controller is responsible only for handling equipment-related operations
public class EquipmentController {
    private final EquipmentFactory equipmentFactory;
    
    public EquipmentController() {
        this.equipmentFactory = new EquipmentFactory();
    }
    
    // Get all equipment
    public List<Equipment> getAllEquipment() {
        List<Equipment> equipmentList = new ArrayList<>();
        String query = "SELECT * FROM equipment";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                int id = rs.getInt("equipment_id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                String status = rs.getString("status");
                int conditionRating = rs.getInt("condition_rating");
                
                Equipment equipment = equipmentFactory.createEquipmentFromDatabase(
                    id, name, type, status, conditionRating);
                
                equipmentList.add(equipment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return equipmentList;
    }
    
    // Get equipment by ID
    public Equipment getEquipmentById(int id) {
        String query = "SELECT * FROM equipment WHERE equipment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                String type = rs.getString("type");
                String status = rs.getString("status");
                int conditionRating = rs.getInt("condition_rating");
                
                return equipmentFactory.createEquipmentFromDatabase(
                    id, name, type, status, conditionRating);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Add new equipment
    public boolean addEquipment(Equipment equipment) {
        String query = "INSERT INTO equipment (name, type, status, condition_rating) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, equipment.getName());
            stmt.setString(2, equipment.getType());
            stmt.setString(3, equipment.getStatus());
            stmt.setInt(4, equipment.getConditionRating());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    equipment.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Update equipment
    public boolean updateEquipment(Equipment equipment) {
        String query = "UPDATE equipment SET name = ?, type = ?, status = ?, condition_rating = ? WHERE equipment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, equipment.getName());
            stmt.setString(2, equipment.getType());
            stmt.setString(3, equipment.getStatus());
            stmt.setInt(4, equipment.getConditionRating());
            stmt.setInt(5, equipment.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Delete equipment
    public boolean deleteEquipment(int id) {
        String query = "DELETE FROM equipment WHERE equipment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Checkout equipment
    public boolean checkoutEquipment(int equipmentId, int userId) {
        Equipment equipment = getEquipmentById(equipmentId);
        if (equipment == null || !equipment.getStatus().equals("AVAILABLE")) {
            return false;
        }
        
        String updateQuery = "UPDATE equipment SET status = 'CHECKED_OUT', last_checked_out = CURRENT_TIMESTAMP WHERE equipment_id = ?";
        String logQuery = "INSERT INTO equipment_checkout (equipment_id, user_id) VALUES (?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Update equipment status
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, equipmentId);
                updateStmt.executeUpdate();
            }
            
            // Log checkout
            try (PreparedStatement logStmt = conn.prepareStatement(logQuery)) {
                logStmt.setInt(1, equipmentId);
                logStmt.setInt(2, userId);
                logStmt.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException commitEx) {
                commitEx.printStackTrace();
            }
        }
        
        return false;
    }
    
    // Return equipment
    public boolean returnEquipment(int equipmentId, int userId) {
        Equipment equipment = getEquipmentById(equipmentId);
        if (equipment == null || !equipment.getStatus().equals("CHECKED_OUT")) {
            return false;
        }
        
        String updateQuery = "UPDATE equipment SET status = 'AVAILABLE' WHERE equipment_id = ?";
        String logQuery = "UPDATE equipment_checkout SET return_time = CURRENT_TIMESTAMP " +
                         "WHERE equipment_id = ? AND user_id = ? AND return_time IS NULL";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Update equipment status
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, equipmentId);
                updateStmt.executeUpdate();
            }
            
            // Log return
            try (PreparedStatement logStmt = conn.prepareStatement(logQuery)) {
                logStmt.setInt(1, equipmentId);
                logStmt.setInt(2, userId);
                logStmt.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException commitEx) {
                commitEx.printStackTrace();
            }
        }
        
        return false;
    }
    
    // Add feedback for equipment
    public boolean addFeedback(EquipmentFeedback feedback) {
        String query = "INSERT INTO equipment_feedback (equipment_id, user_id, rating, comments) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, feedback.getEquipmentId());
            stmt.setInt(2, feedback.getUserId());
            stmt.setInt(3, feedback.getRating());
            stmt.setString(4, feedback.getComments());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    feedback.setId(generatedKeys.getInt(1));
                }
                
                // Also update the equipment condition rating based on this feedback
                updateEquipmentRating(feedback.getEquipmentId());
                
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Update equipment rating based on feedback
    private void updateEquipmentRating(int equipmentId) {
        String query = "SELECT AVG(rating) as avg_rating FROM equipment_feedback WHERE equipment_id = ?";
        String updateQuery = "UPDATE equipment SET condition_rating = ? WHERE equipment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, equipmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int avgRating = (int) Math.round(rs.getDouble("avg_rating"));
                
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, avgRating);
                    updateStmt.setInt(2, equipmentId);
                    updateStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Get all feedback for a specific equipment
    public List<EquipmentFeedback> getFeedbackForEquipment(int equipmentId) {
        List<EquipmentFeedback> feedbackList = new ArrayList<>();
        String query = "SELECT * FROM equipment_feedback WHERE equipment_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, equipmentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                EquipmentFeedback feedback = new EquipmentFeedback();
                feedback.setId(rs.getInt("feedback_id"));
                feedback.setEquipmentId(rs.getInt("equipment_id"));
                feedback.setUserId(rs.getInt("user_id"));
                feedback.setRating(rs.getInt("rating"));
                feedback.setComments(rs.getString("comments"));
                feedback.setCreatedAt(rs.getTimestamp("created_at"));
                
                feedbackList.add(feedback);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return feedbackList;
    }
}