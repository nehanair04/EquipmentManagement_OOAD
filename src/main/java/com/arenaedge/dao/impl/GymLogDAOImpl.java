package com.arenaedge.dao.impl;

import com.arenaedge.dao.GymLogDAO;
import com.arenaedge.model.gym.GymLog;
import com.arenaedge.model.gym.LogType;
import com.arenaedge.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of the GymLogDAO interface.
 */
public class GymLogDAOImpl implements GymLogDAO {

    @Override
    public void saveLog(GymLog log) {
        String sql = "INSERT INTO gym_logs (user_id, timestamp, log_type) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, log.getUserId());
            ps.setTimestamp(2, Timestamp.valueOf(log.getTimestamp()));
            ps.setString(3, log.getType().name());
            ps.executeUpdate();
            
            // Get the generated ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    log.setLogId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving gym log", e);
        }
    }

    @Override
    public void deleteLog(int logId) {
        String sql = "DELETE FROM gym_logs WHERE log_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, logId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting gym log", e);
        }
    }

    @Override
    public GymLog getLogById(int logId) {
        String sql = "SELECT * FROM gym_logs WHERE log_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, logId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGymLog(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving gym log", e);
        }
        
        return null;
    }

    @Override
    public List<GymLog> getLogsByUser(int userId) {
        String sql = "SELECT * FROM gym_logs WHERE user_id = ? ORDER BY timestamp";
        List<GymLog> logs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToGymLog(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving user gym logs", e);
        }
        
        return logs;
    }

    @Override
    public List<GymLog> getLogsByType(LogType type) {
        String sql = "SELECT * FROM gym_logs WHERE log_type = ? ORDER BY timestamp";
        List<GymLog> logs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, type.name());
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToGymLog(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving gym logs by type", e);
        }
        
        return logs;
    }

    @Override
    public List<GymLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM gym_logs WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp";
        List<GymLog> logs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToGymLog(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving gym logs by date range", e);
        }
        
        return logs;
    }

    @Override
    public List<GymLog> getUnmatchedEntries() {
        // This query finds entry logs that don't have a matching exit log
        String sql = "SELECT e.* FROM gym_logs e " +
                     "LEFT JOIN gym_logs x ON e.user_id = x.user_id AND x.log_type = 'EXIT' AND " +
                     "x.timestamp > e.timestamp " +
                     "WHERE e.log_type = 'ENTRY' AND x.log_id IS NULL";
        
        List<GymLog> logs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToGymLog(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving unmatched entry logs", e);
        }
        
        return logs;
    }
    
    /**
     * Helper method to map ResultSet to GymLog object
     */
    private GymLog mapResultSetToGymLog(ResultSet rs) throws SQLException {
        GymLog log = new GymLog();
        log.setLogId(rs.getInt("log_id"));
        log.setUserId(rs.getInt("user_id"));
        log.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        log.setType(LogType.valueOf(rs.getString("log_type")));
        return log;
    }
}