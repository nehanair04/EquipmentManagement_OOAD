package com.arenaedge.model.membership;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arenaedge.util.DatabaseConnection;

/**
 * Singleton manager for memberships.
 * Implements the Singleton Pattern for centralized membership management.
 */
public class MembershipManager {
    // Singleton instance
    private static MembershipManager instance;
    
    // Membership type pricing (could be loaded from database in a real app)
    private Map<String, Double> membershipPrices;
    
    /**
     * Private constructor to prevent direct instantiation
     */
    private MembershipManager() {
        // Initialize membership prices
        membershipPrices = new HashMap<>();
        membershipPrices.put("BASIC", 49.99);
        membershipPrices.put("PREMIUM", 79.99);
        membershipPrices.put("GOLD", 99.99);
    }
    
    /**
     * Get the singleton instance
     * 
     * @return the single instance of MembershipManager
     */
    public static synchronized MembershipManager getInstance() {
        if (instance == null) {
            instance = new MembershipManager();
        }
        return instance;
    }
    
    /**
     * Get all memberships from the database
     * 
     * @return list of all memberships
     */
    public List<Membership> getAllMemberships() {
        List<Membership> memberships = new ArrayList<>();
        String query = "SELECT * FROM memberships";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Membership membership = mapResultSetToMembership(rs);
                memberships.add(membership);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return memberships;
    }
    
    /**
     * Get memberships for a specific user
     * 
     * @param userId the user ID
     * @return list of user's memberships
     */
    public List<Membership> getMembershipsByUser(int userId) {
        List<Membership> memberships = new ArrayList<>();
        String query = "SELECT * FROM memberships WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Membership membership = mapResultSetToMembership(rs);
                    memberships.add(membership);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return memberships;
    }
    
    /**
     * Get a membership by ID
     * 
     * @param membershipId the membership ID
     * @return the membership or null if not found
     */
    public Membership getMembershipById(int membershipId) {
        String query = "SELECT * FROM memberships WHERE membership_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, membershipId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMembership(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Create a new membership
     * 
     * @param membership the membership to create
     * @return true if successful
     */
    public boolean createMembership(Membership membership) {
        String query = "INSERT INTO memberships (user_id, type, start_date, end_date, fee, active, payment_status) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, membership.getUserId());
            stmt.setString(2, membership.getType());
            stmt.setTimestamp(3, membership.getStartDate());
            stmt.setTimestamp(4, membership.getEndDate());
            stmt.setDouble(5, membership.getFee());
            stmt.setBoolean(6, membership.isActive());
            stmt.setString(7, membership.getPaymentStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        membership.setMembershipId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Update an existing membership
     * 
     * @param membership the membership to update
     * @return true if successful
     */
    public boolean updateMembership(Membership membership) {
        String query = "UPDATE memberships SET user_id = ?, type = ?, start_date = ?, end_date = ?, " +
                       "fee = ?, active = ?, payment_status = ? WHERE membership_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, membership.getUserId());
            stmt.setString(2, membership.getType());
            stmt.setTimestamp(3, membership.getStartDate());
            stmt.setTimestamp(4, membership.getEndDate());
            stmt.setDouble(5, membership.getFee());
            stmt.setBoolean(6, membership.isActive());
            stmt.setString(7, membership.getPaymentStatus());
            stmt.setInt(8, membership.getMembershipId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Delete a membership
     * 
     * @param membershipId the ID of the membership to delete
     * @return true if successful
     */
    public boolean deleteMembership(int membershipId) {
        String query = "DELETE FROM memberships WHERE membership_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, membershipId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get the standard price for a membership type
     * 
     * @param type the membership type
     * @return the price for that type
     */
    public double getMembershipPrice(String type) {
        return membershipPrices.getOrDefault(type, 0.0);
    }
    
    /**
     * Process a payment for a membership
     * 
     * @param userId the user ID
     * @param membershipId the membership ID
     * @param amount the payment amount
     * @param paymentMethod the payment method
     * @return the payment object if successful, null otherwise
     */
    public Payment processPayment(int userId, int membershipId, double amount, String paymentMethod) {
        // Create payment object
        Payment payment = new Payment(userId, amount, paymentMethod);
        payment.setMembershipId(membershipId);
        
        // Process the payment
        boolean success = payment.process();
        
        if (success) {
            // Update the membership payment status
            Membership membership = getMembershipById(membershipId);
            if (membership != null) {
                membership.setPaymentStatus("PAID");
                updateMembership(membership);
            }
            
            // Save payment to database
            savePayment(payment);
        }
        
        return success ? payment : null;
    }
    
    /**
     * Save a payment to the database
     * 
     * @param payment the payment to save
     * @return true if successful
     */
    private boolean savePayment(Payment payment) {
        String query = "INSERT INTO payments (user_id, membership_id, booking_id, amount, payment_method, " +
                       "status, payment_date, transaction_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, payment.getUserId());
            stmt.setInt(2, payment.getMembershipId());
            stmt.setInt(3, payment.getBookingId());
            stmt.setDouble(4, payment.getAmount());
            stmt.setString(5, payment.getPaymentMethod());
            stmt.setString(6, payment.getStatus());
            stmt.setTimestamp(7, payment.getPaymentDate());
            stmt.setString(8, payment.getTransactionId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        payment.setPaymentId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get all payments for a user
     * 
     * @param userId the user ID
     * @return list of payments
     */
    public List<Payment> getPaymentsByUser(int userId) {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT * FROM payments WHERE user_id = ? ORDER BY payment_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = mapResultSetToPayment(rs);
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return payments;
    }
    
    /**
     * Get all payments for a membership
     * 
     * @param membershipId the membership ID
     * @return list of payments
     */
    public List<Payment> getPaymentsByMembership(int membershipId) {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT * FROM payments WHERE membership_id = ? ORDER BY payment_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, membershipId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = mapResultSetToPayment(rs);
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return payments;
    }
    
    /**
     * Map a ResultSet to a Membership object
     * 
     * @param rs the ResultSet
     * @return the Membership object
     * @throws SQLException if mapping fails
     */
    private Membership mapResultSetToMembership(ResultSet rs) throws SQLException {
        Membership membership = new Membership();
        membership.setMembershipId(rs.getInt("membership_id"));
        membership.setUserId(rs.getInt("user_id"));
        membership.setType(rs.getString("type"));
        membership.setStartDate(rs.getTimestamp("start_date"));
        membership.setEndDate(rs.getTimestamp("end_date"));
        membership.setFee(rs.getDouble("fee"));
        membership.setActive(rs.getBoolean("active"));
        membership.setPaymentStatus(rs.getString("payment_status"));
        return membership;
    }
    
    /**
     * Map a ResultSet to a Payment object
     * 
     * @param rs the ResultSet
     * @return the Payment object
     * @throws SQLException if mapping fails
     */
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setUserId(rs.getInt("user_id"));
        payment.setMembershipId(rs.getInt("membership_id"));
        payment.setBookingId(rs.getInt("booking_id"));
        payment.setAmount(rs.getDouble("amount"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setStatus(rs.getString("status"));
        payment.setPaymentDate(rs.getTimestamp("payment_date"));
        payment.setTransactionId(rs.getString("transaction_id"));
        return payment;
    }
}
