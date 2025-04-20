package com.arenaedge.view.membership;

import com.arenaedge.controller.MembershipController;
import com.arenaedge.model.membership.Membership;
import com.arenaedge.model.membership.Payment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Panel for managing memberships.
 */
public class MembershipPanel extends JPanel {
    private MembershipController controller;
    
    private JTable membershipTable;
    private DefaultTableModel tableModel;
    private JButton createBtn, renewBtn, paymentBtn, cancelBtn, detailsBtn;
    private JComboBox<String> membershipTypeCombo;
    
    // Current user ID (hardcoded for demo purposes)
    private final int currentUserId = 1;
    
    /**
     * Constructor for MembershipPanel
     */
    public MembershipPanel() {
        this.controller = new MembershipController();
        
        setLayout(new BorderLayout());
        
        // Create the main components
        createTablePanel();
        createFormPanel();
        createActionPanel();
        
        // Load data initially
        refreshTableData();
    }
    
    /**
     * Create the table panel
     */
    private void createTablePanel() {
        // Create table model with column names
        String[] columns = {"ID", "Type", "Start Date", "End Date", "Fee", "Status", "Days Left"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        membershipTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(membershipTable);
        
        // Single selection mode
        membershipTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add to panel
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Create the form panel for creating memberships
     */
    private void createFormPanel() {
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Membership"));
        
        JLabel typeLabel = new JLabel("Membership Type:");
        membershipTypeCombo = new JComboBox<>(new String[]{"BASIC", "PREMIUM", "GOLD"});
        
        JLabel durationLabel = new JLabel("Duration (months):");
        JComboBox<Integer> durationCombo = new JComboBox<>(new Integer[]{1, 3, 6, 12});
        
        createBtn = new JButton("Create Membership");
        
        // Add action for creating a new membership
        createBtn.addActionListener(e -> {
            String type = (String) membershipTypeCombo.getSelectedItem();
            int duration = (int) durationCombo.getSelectedItem();
            
            Membership membership = controller.createMembership(currentUserId, type, duration);
            
            if (membership != null) {
                // Show payment dialog
                String[] paymentMethods = {"CREDIT_CARD", "DEBIT_CARD", "CASH", "BANK_TRANSFER"};
                String selectedMethod = (String) JOptionPane.showInputDialog(
                    this,
                    "Select Payment Method:",
                    "Payment Required",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    paymentMethods,
                    paymentMethods[0]
                );
                
                if (selectedMethod != null) {
                    String receipt = controller.processPayment(
                        currentUserId, membership.getMembershipId(), membership.getFee(), selectedMethod);
                    
                    if (receipt != null) {
                        JOptionPane.showMessageDialog(
                            this,
                            "Membership created successfully.\n\n" + receipt,
                            "Payment Successful",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        refreshTableData();
                    } else {
                        JOptionPane.showMessageDialog(
                            this,
                            "Payment failed. Please try again.",
                            "Payment Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to create membership",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        
        // Add components to panel
        formPanel.add(typeLabel);
        formPanel.add(membershipTypeCombo);
        formPanel.add(durationLabel);
        formPanel.add(durationCombo);
        formPanel.add(createBtn);
        
        // Add to main panel
        add(formPanel, BorderLayout.NORTH);
    }
    
    /**
     * Create the action panel
     */
    private void createActionPanel() {
        JPanel actionPanel = new JPanel();
        
        renewBtn = new JButton("Renew");
        paymentBtn = new JButton("Payment History");
        cancelBtn = new JButton("Cancel");
        detailsBtn = new JButton("Details");
        
        // Add action listeners
        renewBtn.addActionListener(e -> handleRenew());
        paymentBtn.addActionListener(e -> handlePaymentHistory());
        cancelBtn.addActionListener(e -> handleCancel());
        detailsBtn.addActionListener(e -> handleDetails());
        
        // Add buttons to panel
        actionPanel.add(renewBtn);
        actionPanel.add(paymentBtn);
        actionPanel.add(cancelBtn);
        actionPanel.add(detailsBtn);
        
        // Add to main panel
        add(actionPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Refresh the table data
     */
    private void refreshTableData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get all memberships for current user
        List<Membership> memberships = controller.getMembershipsByUser(currentUserId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        // Add each membership to table model
        for (Membership membership : memberships) {
            // Calculate days remaining
            int daysLeft = membership.daysRemaining();
            
            Object[] row = {
                membership.getMembershipId(),
                membership.getType(),
                dateFormat.format(membership.getStartDate()),
                dateFormat.format(membership.getEndDate()),
                String.format("$%.2f", membership.getFee()),
                membership.isActive() ? membership.getPaymentStatus() : "EXPIRED",
                daysLeft
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * Handle renewing a membership
     */
    private void handleRenew() {
        int selectedRow = membershipTable.getSelectedRow();
        if (selectedRow >= 0) {
            int membershipId = (int) tableModel.getValueAt(selectedRow, 0);
            Membership membership = controller.getMembershipById(membershipId);
            
            if (membership != null) {
                // Show renewal dialog
                Integer[] durations = {1, 3, 6, 12};
                Integer selectedDuration = (Integer) JOptionPane.showInputDialog(
                    this,
                    "Select renewal duration (months):",
                    "Renew Membership",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    durations,
                    durations[2] // Default to 6 months
                );
                
                if (selectedDuration != null) {
                    String[] paymentMethods = {"CREDIT_CARD", "DEBIT_CARD", "CASH", "BANK_TRANSFER"};
                    String selectedMethod = (String) JOptionPane.showInputDialog(
                        this,
                        "Select Payment Method:",
                        "Payment Required",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        paymentMethods,
                        paymentMethods[0]
                    );
                    
                    if (selectedMethod != null) {
                        String receipt = controller.renewMembership(membershipId, selectedDuration, selectedMethod);
                        
                        if (receipt != null) {
                            JOptionPane.showMessageDialog(
                                this,
                                "Membership renewed successfully.\n\n" + receipt,
                                "Renewal Successful",
                                JOptionPane.INFORMATION_MESSAGE
                            );
                            refreshTableData();
                        } else {
                            JOptionPane.showMessageDialog(
                                this,
                                "Renewal payment failed. Please try again.",
                                "Payment Error",
                                JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Please select a membership to renew",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }
    
    /**
     * Handle viewing payment history
     */
    private void handlePaymentHistory() {
        int selectedRow = membershipTable.getSelectedRow();
        
        if (selectedRow >= 0) {
            int membershipId = (int) tableModel.getValueAt(selectedRow, 0);
            
            // Create a dialog to show payment history
            JDialog paymentDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Payment History", true);
            paymentDialog.setLayout(new BorderLayout());
            paymentDialog.setSize(600, 400);
            paymentDialog.setLocationRelativeTo(this);
            
            // Create table for payments
            String[] columns = {"ID", "Date", "Amount", "Method", "Status", "Transaction ID"};
            DefaultTableModel paymentModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            JTable paymentTable = new JTable(paymentModel);
            JScrollPane scrollPane = new JScrollPane(paymentTable);
            
            // Get payments for this membership
            List<Payment> payments = controller.getPaymentsByMembership(membershipId);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            // Add payments to table
            for (Payment payment : payments) {
                Object[] row = {
                    payment.getPaymentId(),
                    dateFormat.format(payment.getPaymentDate()),
                    String.format("$%.2f", payment.getAmount()),
                    payment.getPaymentMethod(),
                    payment.getStatus(),
                    payment.getTransactionId()
                };
                paymentModel.addRow(row);
            }
            
            // Add close button
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> paymentDialog.dispose());
            
            // Add components to dialog
            paymentDialog.add(scrollPane, BorderLayout.CENTER);
            paymentDialog.add(closeButton, BorderLayout.SOUTH);
            
            // Show dialog
            paymentDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Please select a membership to view payment history",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }
    
    /**
     * Handle canceling a membership
     */
    private void handleCancel() {
        int selectedRow = membershipTable.getSelectedRow();
        if (selectedRow >= 0) {
            int membershipId = (int) tableModel.getValueAt(selectedRow, 0);
            
            // Confirm cancellation
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel this membership?\nThis action cannot be undone.",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = controller.cancelMembership(membershipId);
                
                if (success) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Membership cancelled successfully",
                        "Cancellation Successful",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    refreshTableData();
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Failed to cancel membership",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Please select a membership to cancel",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }
    
    /**
     * Handle showing membership details
     */
    private void handleDetails() {
        int selectedRow = membershipTable.getSelectedRow();
        if (selectedRow >= 0) {
            int membershipId = (int) tableModel.getValueAt(selectedRow, 0);
            Membership membership = controller.getMembershipById(membershipId);
            
            if (membership != null) {
                // Format dates
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                
                // Build details message
                StringBuilder details = new StringBuilder();
                details.append("Membership ID: ").append(membership.getMembershipId()).append("\n");
                details.append("User ID: ").append(membership.getUserId()).append("\n");
                details.append("Type: ").append(membership.getType()).append("\n");
                details.append("Start Date: ").append(dateFormat.format(membership.getStartDate())).append("\n");
                details.append("End Date: ").append(dateFormat.format(membership.getEndDate())).append("\n");
                details.append("Fee: $").append(String.format("%.2f", membership.getFee())).append("\n");
                details.append("Status: ").append(membership.isActive() ? "Active" : "Inactive").append("\n");
                details.append("Payment Status: ").append(membership.getPaymentStatus()).append("\n");
                details.append("Days Remaining: ").append(membership.daysRemaining()).append("\n");
                details.append("Valid: ").append(membership.isValid() ? "Yes" : "No");
                
                // Show details dialog
                JOptionPane.showMessageDialog(
                    this,
                    details.toString(),
                    "Membership Details",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Please select a membership to view details",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }
}
