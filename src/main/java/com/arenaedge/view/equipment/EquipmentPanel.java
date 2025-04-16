package com.arenaedge.view.equipment;

import com.arenaedge.controller.EquipmentController;
import com.arenaedge.model.equipment.Equipment;
import com.arenaedge.model.equipment.factory.EquipmentFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.List;

public class EquipmentPanel extends JPanel {
    private final EquipmentController controller;
    private final EquipmentFactory factory;
    
    private JTable equipmentTable;
    private DefaultTableModel tableModel;
    private JButton checkoutBtn, returnBtn, addBtn, feedbackBtn, deleteBtn;
    private JComboBox<String> equipmentTypeCombo;
    
    // Current user ID (hardcoded for demo purposes)
    private final int currentUserId = 1;  
    
    public EquipmentPanel() {
        controller = new EquipmentController();
        factory = new EquipmentFactory();
        
        setLayout(new BorderLayout());
        
        // Create table to display equipment
        createTablePanel();
        
        // Create action buttons
        createActionPanel();
        
        // Create add equipment panel
        createAddEquipmentPanel();
        
        // Load data initially
        refreshTableData();
    }
    
    private void createTablePanel() {
        // Create table model with column names
        String[] columns = {"ID", "Name", "Type", "Status", "Condition"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        equipmentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(equipmentTable);
        
        // Single selection mode
        equipmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void createActionPanel() {
        JPanel actionPanel = new JPanel();
        
        checkoutBtn = new JButton("Checkout");
        returnBtn = new JButton("Return");
        feedbackBtn = new JButton("Provide Feedback");
        deleteBtn = new JButton("Delete");
        
        // Add action listeners
        checkoutBtn.addActionListener(this::handleCheckout);
        returnBtn.addActionListener(this::handleReturn);
        feedbackBtn.addActionListener(this::handleFeedback);
        deleteBtn.addActionListener(this::handleDelete);
        
        actionPanel.add(checkoutBtn);
        actionPanel.add(returnBtn);
        actionPanel.add(feedbackBtn);
        actionPanel.add(deleteBtn);
        
        add(actionPanel, BorderLayout.SOUTH);
    }
    
    private void createAddEquipmentPanel() {
        JPanel addPanel = new JPanel();
        JLabel typeLabel = new JLabel("Type:");
        JLabel nameLabel = new JLabel("Name:");
        
        equipmentTypeCombo = new JComboBox<>(new String[]{"BASKETBALL", "TENNIS", "BADMINTON"});
        JTextField nameField = new JTextField(15);
        
        addBtn = new JButton("Add New Equipment");
        
        addBtn.addActionListener(e -> {
            String type = (String) equipmentTypeCombo.getSelectedItem();
            String name = nameField.getText().trim();
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a name for the equipment", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Equipment newEquipment = factory.createEquipment(type);
            newEquipment.setName(name);
            
            if (controller.addEquipment(newEquipment)) {
                JOptionPane.showMessageDialog(this, 
                    "Equipment added successfully", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                nameField.setText("");
                refreshTableData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to add equipment", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        addPanel.add(typeLabel);
        addPanel.add(equipmentTypeCombo);
        addPanel.add(nameLabel);
        addPanel.add(nameField);
        addPanel.add(addBtn);
        
        add(addPanel, BorderLayout.NORTH);
    }
    
    private void refreshTableData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get all equipment from controller
        List<Equipment> equipmentList = controller.getAllEquipment();
        
        // Add each equipment to table model
        for (Equipment equipment : equipmentList) {
            Object[] row = {
                equipment.getId(),
                equipment.getName(),
                equipment.getType(),
                equipment.getStatus(),
                equipment.getConditionRating()
            };
            tableModel.addRow(row);
        }
    }
    
    private void handleCheckout(ActionEvent e) {
        int selectedRow = equipmentTable.getSelectedRow();
        if (selectedRow >= 0) {
            int equipmentId = (int) tableModel.getValueAt(selectedRow, 0);
            String status = (String) tableModel.getValueAt(selectedRow, 3);
            
            if (!"AVAILABLE".equals(status)) {
                JOptionPane.showMessageDialog(this, 
                    "Equipment is not available for checkout", 
                    "Checkout Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (controller.checkoutEquipment(equipmentId, currentUserId)) {
                JOptionPane.showMessageDialog(this, 
                    "Equipment checked out successfully", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTableData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to checkout equipment", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select an equipment first", 
                "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void handleReturn(ActionEvent e) {
        int selectedRow = equipmentTable.getSelectedRow();
        if (selectedRow >= 0) {
            int equipmentId = (int) tableModel.getValueAt(selectedRow, 0);
            String status = (String) tableModel.getValueAt(selectedRow, 3);
            
            if (!"CHECKED_OUT".equals(status)) {
                JOptionPane.showMessageDialog(this, 
                    "Equipment is not checked out", 
                    "Return Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (controller.returnEquipment(equipmentId, currentUserId)) {
                // Show feedback dialog on return
                showFeedbackDialog(equipmentId);
                
                JOptionPane.showMessageDialog(this, 
                    "Equipment returned successfully", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTableData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to return equipment", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select an equipment first", 
                "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void handleFeedback(ActionEvent e) {
        int selectedRow = equipmentTable.getSelectedRow();
        if (selectedRow >= 0) {
            int equipmentId = (int) tableModel.getValueAt(selectedRow, 0);
            showFeedbackDialog(equipmentId);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select an equipment first", 
                "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void showFeedbackDialog(int equipmentId) {
        JDialog feedbackDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Equipment Feedback", true);
        feedbackDialog.setLayout(new BorderLayout());
        feedbackDialog.setSize(400, 300);
        feedbackDialog.setLocationRelativeTo(this);
        
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        JLabel ratingLabel = new JLabel("Rating (1-5):");
        JComboBox<Integer> ratingCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        
        JLabel commentsLabel = new JLabel("Comments:");
        JTextArea commentsArea = new JTextArea(5, 20);
        JScrollPane commentsScroll = new JScrollPane(commentsArea);
        
        inputPanel.add(ratingLabel);
        inputPanel.add(ratingCombo);
        inputPanel.add(commentsLabel);
        inputPanel.add(commentsScroll);
        
        JButton submitBtn = new JButton("Submit Feedback");
        
        submitBtn.addActionListener(event -> {
            int rating = (int) ratingCombo.getSelectedItem();
            String comments = commentsArea.getText();
            
            com.arenaedge.model.feedback.EquipmentFeedback feedback = 
                new com.arenaedge.model.feedback.EquipmentFeedback(equipmentId, currentUserId, rating, comments);
            
            if (controller.addFeedback(feedback)) {
                JOptionPane.showMessageDialog(feedbackDialog, 
                    "Feedback submitted successfully", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                feedbackDialog.dispose();
                refreshTableData();
            } else {
                JOptionPane.showMessageDialog(feedbackDialog, 
                    "Failed to submit feedback", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        feedbackDialog.add(inputPanel, BorderLayout.CENTER);
        feedbackDialog.add(submitBtn, BorderLayout.SOUTH);
        
        feedbackDialog.setVisible(true);
    }
    
    private void handleDelete(ActionEvent e) {
        int selectedRow = equipmentTable.getSelectedRow();
        if (selectedRow >= 0) {
            int equipmentId = (int) tableModel.getValueAt(selectedRow, 0);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this equipment?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                if (controller.deleteEquipment(equipmentId)) {
                    JOptionPane.showMessageDialog(this, 
                        "Equipment deleted successfully", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshTableData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete equipment", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select an equipment first", 
                "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}