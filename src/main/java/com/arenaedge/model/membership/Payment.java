package com.arenaedge.model.membership;

import java.sql.Timestamp;

/**
 * Represents a payment in the system.
 * Follows the Information Expert principle by containing all payment-related data.
 */
public class Payment {
    private int paymentId;
    private int userId;
    private int membershipId;  // Optional, can be 0 for non-membership payments
    private int bookingId;     // Optional, can be 0 for non-booking payments
    private double amount;
    private String paymentMethod; // "CREDIT_CARD", "DEBIT_CARD", "CASH", "BANK_TRANSFER"
    private String status;        // "COMPLETED", "PENDING", "FAILED"
    private Timestamp paymentDate;
    private String transactionId;  // External payment reference
    
    /**
     * Default constructor
     */
    public Payment() {
        this.status = "PENDING";
        this.paymentDate = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Constructor with essential fields
     */
    public Payment(int userId, double amount, String paymentMethod) {
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = "PENDING";
        this.paymentDate = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Process the payment
     * In a real system, this would connect to a payment gateway
     * 
     * @return true if payment is successful
     */
    public boolean process() {
        // Simulate payment processing
        boolean success = Math.random() > 0.1; // 90% success rate
        
        if (success) {
            this.status = "COMPLETED";
            this.transactionId = "TXN" + System.currentTimeMillis();
        } else {
            this.status = "FAILED";
        }
        
        return success;
    }
    
    /**
     * Generate a receipt for the payment
     * 
     * @return a receipt string
     */
    public String generateReceipt() {
        if (!"COMPLETED".equals(this.status)) {
            return "Cannot generate receipt for incomplete payment";
        }
        
        StringBuilder receipt = new StringBuilder();
        receipt.append("===== PAYMENT RECEIPT =====\n");
        receipt.append("Payment ID: ").append(paymentId).append("\n");
        receipt.append("User ID: ").append(userId).append("\n");
        
        if (membershipId > 0) {
            receipt.append("Membership ID: ").append(membershipId).append("\n");
        }
        
        if (bookingId > 0) {
            receipt.append("Booking ID: ").append(bookingId).append("\n");
        }
        
        receipt.append("Amount: $").append(String.format("%.2f", amount)).append("\n");
        receipt.append("Payment Method: ").append(paymentMethod).append("\n");
        receipt.append("Date: ").append(paymentDate).append("\n");
        receipt.append("Transaction ID: ").append(transactionId).append("\n");
        receipt.append("Status: ").append(status).append("\n");
        receipt.append("===========================\n");
        
        return receipt.toString();
    }

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(int membershipId) {
        this.membershipId = membershipId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
