package com.arenaedge.controller;

import com.arenaedge.model.membership.Membership;
import com.arenaedge.model.membership.MembershipManager;
import com.arenaedge.model.membership.Payment;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for membership-related operations.
 * Acts as an intermediary between the UI and the MembershipManager.
 */
public class MembershipController {
    private final MembershipManager membershipManager;
    
    /**
     * Constructor for MembershipController
     */
    public MembershipController() {
        this.membershipManager = MembershipManager.getInstance();
    }
    
    /**
     * Get all memberships
     * 
     * @return list of all memberships
     */
    public List<Membership> getAllMemberships() {
        return membershipManager.getAllMemberships();
    }
    
    /**
     * Get memberships for a specific user
     * 
     * @param userId the user ID
     * @return list of user's memberships
     */
    public List<Membership> getMembershipsByUser(int userId) {
        return membershipManager.getMembershipsByUser(userId);
    }
    
    /**
     * Get a specific membership by ID
     * 
     * @param membershipId the membership ID
     * @return the membership or null if not found
     */
    public Membership getMembershipById(int membershipId) {
        return membershipManager.getMembershipById(membershipId);
    }
    
    /**
     * Create a new membership with the given details
     * 
     * @param userId the user ID
     * @param type the membership type
     * @param durationMonths the membership duration in months
     * @return the created membership or null if creation failed
     */
    public Membership createMembership(int userId, String type, int durationMonths) {
        // Get price for the membership type
        double fee = membershipManager.getMembershipPrice(type);
        
        // Calculate start and end dates
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusMonths(durationMonths);
        
        // Create the membership object
        Membership membership = new Membership();
        membership.setUserId(userId);
        membership.setType(type);
        membership.setStartDate(Timestamp.valueOf(now));
        membership.setEndDate(Timestamp.valueOf(endDate));
        membership.setFee(fee);
        membership.setActive(true);
        membership.setPaymentStatus("PENDING");
        
        // Save to database
        boolean success = membershipManager.createMembership(membership);
        
        return success ? membership : null;
    }
    
    /**
     * Update an existing membership
     * 
     * @param membership the membership to update
     * @return true if update was successful
     */
    public boolean updateMembership(Membership membership) {
        return membershipManager.updateMembership(membership);
    }
    
    /**
     * Cancel a membership
     * 
     * @param membershipId the ID of the membership to cancel
     * @return true if cancellation was successful
     */
    public boolean cancelMembership(int membershipId) {
        Membership membership = membershipManager.getMembershipById(membershipId);
        if (membership != null) {
            membership.cancel();
            return membershipManager.updateMembership(membership);
        }
        return false;
    }
    
    /**
     * Process a payment for a membership
     * 
     * @param userId the user ID
     * @param membershipId the membership ID
     * @param amount the payment amount
     * @param paymentMethod the payment method
     * @return the payment receipt if successful, null otherwise
     */
    public String processPayment(int userId, int membershipId, double amount, String paymentMethod) {
        Payment payment = membershipManager.processPayment(userId, membershipId, amount, paymentMethod);
        
        if (payment != null) {
            return payment.generateReceipt();
        }
        
        return null;
    }
    
    /**
     * Renew a membership for a specified number of months
     * 
     * @param membershipId the membership ID
     * @param months the number of months to renew
     * @param paymentMethod the payment method
     * @return the payment receipt if successful, null otherwise
     */
    public String renewMembership(int membershipId, int months, String paymentMethod) {
        Membership membership = membershipManager.getMembershipById(membershipId);
        
        if (membership != null) {
            double renewalFee = membership.renew(months);
            
            if (membershipManager.updateMembership(membership)) {
                // Process payment for renewal
                return processPayment(membership.getUserId(), membershipId, renewalFee, paymentMethod);
            }
        }
        
        return null;
    }
    
    /**
     * Get all payments for a user
     * 
     * @param userId the user ID
     * @return list of payments
     */
    public List<Payment> getPaymentsByUser(int userId) {
        return membershipManager.getPaymentsByUser(userId);
    }
    
    /**
     * Get all payments for a membership
     * 
     * @param membershipId the membership ID
     * @return list of payments
     */
    public List<Payment> getPaymentsByMembership(int membershipId) {
        return membershipManager.getPaymentsByMembership(membershipId);
    }
}
