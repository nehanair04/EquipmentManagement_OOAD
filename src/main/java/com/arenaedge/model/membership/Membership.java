package com.arenaedge.model.membership;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Represents a membership in the system.
 * Follows the Information Expert principle by containing all membership-related data and behavior.
 */
public class Membership {
    private int membershipId;
    private int userId;
    private String type;        // "BASIC", "PREMIUM", "GOLD"
    private Timestamp startDate;
    private Timestamp endDate;
    private double fee;
    private boolean active;
    private String paymentStatus; // "PAID", "PENDING", "FAILED"
    
    /**
     * Default constructor
     */
    public Membership() {
        this.active = true;
        this.paymentStatus = "PENDING";
    }
    
    /**
     * Constructor with essential fields
     */
    public Membership(int userId, String type, Timestamp startDate, Timestamp endDate, double fee) {
        this.userId = userId;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fee = fee;
        this.active = true;
        this.paymentStatus = "PENDING";
    }
    
    /**
     * Check if the membership is currently valid
     * 
     * @return true if membership is active and not expired
     */
    public boolean isValid() {
        return active && endDate.after(new Timestamp(System.currentTimeMillis()));
    }
    
    /**
     * Calculate days remaining in membership
     * 
     * @return number of days remaining
     */
    public int daysRemaining() {
        if (!isValid()) return 0;
        
        long currentTime = System.currentTimeMillis();
        long endTime = endDate.getTime();
        long diffTime = endTime - currentTime;
        
        return (int) (diffTime / (1000 * 60 * 60 * 24));
    }
    
    /**
     * Renew membership for a specified number of months
     * 
     * @param months number of months to extend
     * @return the fee for renewal
     */
    public double renew(int months) {
        // Calculate new end date
        LocalDateTime newEndDate = endDate.toLocalDateTime().plusMonths(months);
        this.endDate = Timestamp.valueOf(newEndDate);
        
        // Calculate renewal fee based on membership type and months
        double renewalFee = this.fee * months;
        
        // Apply discount for longer renewals
        if (months >= 6) {
            renewalFee *= 0.9; // 10% discount for 6+ months
        } else if (months >= 12) {
            renewalFee *= 0.8; // 20% discount for 12+ months
        }
        
        return renewalFee;
    }
    
    /**
     * Cancel the membership
     */
    public void cancel() {
        this.active = false;
    }

    // Getters and Setters
    public int getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(int membershipId) {
        this.membershipId = membershipId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    @Override
    public String toString() {
        return "Membership{" +
                "membershipId=" + membershipId +
                ", userId=" + userId +
                ", type='" + type + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", fee=" + fee +
                ", active=" + active +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}
