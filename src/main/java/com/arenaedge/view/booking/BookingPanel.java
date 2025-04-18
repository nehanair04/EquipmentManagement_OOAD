package com.arenaedge.view.booking;

import com.arenaedge.controller.BookingController;
import com.arenaedge.model.booking.Booking;
import com.arenaedge.model.booking.Court;
import com.arenaedge.model.booking.observer.EmailNotifier;
import com.arenaedge.model.booking.observer.SMSNotifier;

import javax.swing.*;

import java.awt.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.arenaedge.util.GreenMailManager;
import javax.mail.internet.MimeMessage;


import java.awt.event.*;
import javax.swing.table.*;


public class BookingPanel extends JPanel {
    private final BookingController controller;
    
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private JComboBox<Court> courtCombo;
    private JButton updateBtn, cancelBtn, refreshBtn, sendRemindersBtn;
    
    // Current user ID (hardcoded for demo purposes)
    private final int currentUserId = 1;
    
    // Date format for display and parsing
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public BookingPanel() {
        controller = new BookingController();
        
        // Add observers for notifications (Observer Pattern)
        controller.addObserver(new EmailNotifier());
        controller.addObserver(new SMSNotifier());
        
        setLayout(new BorderLayout());
        
        // Create table to display bookings
        createTablePanel();
        
        // Create form for adding bookings
        createBookingFormPanel();
        
        // Create action buttons
        createActionPanel();
        
        // Load data initially
        refreshTableData();
    }
    
    private void createTablePanel() {
        // Create table model with column names
        String[] columns = {"ID", "Court", "Type", "Start Time", "End Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        bookingTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        
        // Single selection mode
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void createBookingFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Book a Court"));
        
        JLabel courtLabel = new JLabel("Select Court:");
        courtCombo = new JComboBox<>();
        
        JLabel dateLabel = new JLabel("Date (yyyy-MM-dd):");
        JTextField dateField = new JTextField();
        
        JLabel startTimeLabel = new JLabel("Start Time (HH:mm):");
        JTextField startTimeField = new JTextField();
        
        JLabel durationLabel = new JLabel("Duration (hours):");
        JComboBox<Integer> durationCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4});
        
        JButton bookBtn = new JButton("Book Court");
        
        // Populate court combo box
        List<Court> courts = controller.getAllCourts();
        for (Court court : courts) {
            courtCombo.addItem(court);
        }
        
        // Add action for booking
        bookBtn.addActionListener(e -> {
            try {
                // Get selected court
                Court selectedCourt = (Court) courtCombo.getSelectedItem();
                if (selectedCourt == null) {
                    JOptionPane.showMessageDialog(this, 
                        "Please select a court", 
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Parse date and time
                String dateStr = dateField.getText().trim();
                String timeStr = startTimeField.getText().trim();
                if (dateStr.isEmpty() || timeStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter date and time", 
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Parse date/time
                Date date = dateFormat.parse(dateStr + " " + timeStr);
                Timestamp startTime = new Timestamp(date.getTime());
                
                // Calculate end time based on duration
                int duration = (int) durationCombo.getSelectedItem();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.HOUR_OF_DAY, duration);
                Timestamp endTime = new Timestamp(calendar.getTimeInMillis());
                
                // Create booking
                Booking booking = new Booking(currentUserId, selectedCourt.getId(), 
                                            startTime, endTime, selectedCourt.getType());
                
                if (controller.createBooking(booking)) {
                    JOptionPane.showMessageDialog(this, 
                        "Court booked successfully", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    dateField.setText("");
                    startTimeField.setText("");
                    refreshTableData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to book court. Time slot may not be available.", 
                        "Booking Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid date/time format. Use yyyy-MM-dd for date and HH:mm for time", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        formPanel.add(courtLabel);
        formPanel.add(courtCombo);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(startTimeLabel);
        formPanel.add(startTimeField);
        formPanel.add(durationLabel);
        formPanel.add(durationCombo);
        formPanel.add(new JLabel()); // Empty label for spacing
        formPanel.add(bookBtn);
        
        add(formPanel, BorderLayout.NORTH);
    }

private void showEmailViewer() {
    JDialog emailDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Email Viewer", true);
    emailDialog.setLayout(new BorderLayout());
    emailDialog.setSize(800, 400);
    emailDialog.setLocationRelativeTo(this);
    
    // Table for showing emails
    String[] columns = {"From", "To", "Subject", "Content"};
    DefaultTableModel emailModel = new DefaultTableModel(columns, 0);
    JTable emailTable = new JTable(emailModel);
    JScrollPane scrollPane = new JScrollPane(emailTable);

    // Set column widths - make content column bigger
    emailTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // From
    emailTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // To
    emailTable.getColumnModel().getColumn(2).setPreferredWidth(120);  // Subject
    emailTable.getColumnModel().getColumn(3).setPreferredWidth(520);  // Content - much wider
    
    emailTable.setRowHeight(200);  // Significantly increased row height
    

    
    // Get emails from GreenMail
    try {
        MimeMessage[] messages = GreenMailManager.getInstance().getReceivedMessages();
        
        for (MimeMessage message : messages) {
            Object[] row = {
                message.getFrom()[0],
                message.getAllRecipients()[0],
                message.getSubject(),
                message.getContent().toString()
            };
            emailModel.addRow(row);
        }
        
        if (messages.length == 0) {
            JOptionPane.showMessageDialog(emailDialog, 
                "No emails have been sent yet.", 
                "No Emails", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(emailDialog, 
            "Error retrieving emails: " + ex.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Create button panel with both Clear and Full Screen options
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    
    // Full Screen toggle button
    JToggleButton fullScreenBtn = new JToggleButton("Full Screen");
    final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    fullScreenBtn.addActionListener(e -> {
        if (fullScreenBtn.isSelected()) {
            emailDialog.dispose(); // Dispose the current dialog
            emailDialog.setUndecorated(true); // Remove window decorations
            
            // Store the original size and location for restoration
            final Rectangle originalBounds = emailDialog.getBounds();
            
            // Make it full screen
            device.setFullScreenWindow(emailDialog);
            fullScreenBtn.setText("Exit Full Screen");
            
            // Add ESC key handling to exit full screen mode
            emailDialog.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        device.setFullScreenWindow(null);
                        emailDialog.setUndecorated(false);
                        emailDialog.setBounds(originalBounds);
                        emailDialog.setVisible(true);
                        fullScreenBtn.setSelected(false);
                        fullScreenBtn.setText("Full Screen");
                    }
                }
            });
            emailDialog.setFocusable(true);
            emailDialog.requestFocus();
        } else {
            // Exit full screen mode
            device.setFullScreenWindow(null);
            emailDialog.dispose();
            emailDialog.setUndecorated(false);
            emailDialog.setSize(600, 400);
            emailDialog.setLocationRelativeTo(this);
            emailDialog.setVisible(true);
            fullScreenBtn.setText("Full Screen");
        }
    });
    
    // Clear button
    JButton clearBtn = new JButton("Clear Emails");
    clearBtn.addActionListener(e -> {
        GreenMailManager.getInstance().clearReceivedMessages();
        emailModel.setRowCount(0);
        JOptionPane.showMessageDialog(emailDialog, 
            "All emails cleared", 
            "Success", JOptionPane.INFORMATION_MESSAGE);
    });
    
    // Add buttons to panel
    buttonPanel.add(fullScreenBtn);
    buttonPanel.add(clearBtn);
    
    emailDialog.add(scrollPane, BorderLayout.CENTER);
    emailDialog.add(buttonPanel, BorderLayout.SOUTH);
    
    // Make table rows taller for better readability
    emailTable.setRowHeight(25);
    
    // Add double-click functionality to view email details
    emailTable.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                int row = emailTable.getSelectedRow();
                if (row >= 0) {
                    String subject = (String)emailModel.getValueAt(row, 2);
                    String content = (String)emailModel.getValueAt(row, 3);
                    
                    // Show email content in a separate dialog
                    JDialog contentDialog = new JDialog(emailDialog, "Email: " + subject, true);
                    contentDialog.setLayout(new BorderLayout());
                    contentDialog.setSize(500, 400);
                    contentDialog.setLocationRelativeTo(emailDialog);
                    
                    JEditorPane contentPane = new JEditorPane();
                    contentPane.setContentType("text/html");
                    contentPane.setText(content);
                    contentPane.setEditable(false);
                    
                    contentDialog.add(new JScrollPane(contentPane), BorderLayout.CENTER);
                    contentDialog.setVisible(true);
                }
            }
        }
    });
    
    emailDialog.setVisible(true);
}
    
    private void createActionPanel() {
        JPanel actionPanel = new JPanel();
        
        updateBtn = new JButton("Update");
        cancelBtn = new JButton("Cancel Booking");
        refreshBtn = new JButton("Refresh");
        sendRemindersBtn = new JButton("Send Reminders");
        JButton viewEmailsBtn = new JButton("View Emails");
        
        // Add action listeners
        updateBtn.addActionListener(this::handleUpdate);
        cancelBtn.addActionListener(this::handleCancel);
        refreshBtn.addActionListener(e -> refreshTableData());
        sendRemindersBtn.addActionListener(e -> {
            controller.sendReminders();
            JOptionPane.showMessageDialog(this, 
                "Reminders sent for upcoming bookings", 
                "Reminders", JOptionPane.INFORMATION_MESSAGE);
        });
        
        viewEmailsBtn.addActionListener(e -> showEmailViewer());

        actionPanel.add(updateBtn);
        actionPanel.add(cancelBtn);
        actionPanel.add(refreshBtn);
        actionPanel.add(sendRemindersBtn);
        actionPanel.add(viewEmailsBtn);
        
        add(actionPanel, BorderLayout.SOUTH);
    }
    
    private void refreshTableData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get all bookings for current user
        List<Booking> bookingList = controller.getBookingsForUser(currentUserId);
        
        // Add each booking to table model
        for (Booking booking : bookingList) {
            // Get court name
            Court court = controller.getCourtById(booking.getCourtId());
            String courtName = court != null ? court.getName() : "Unknown";
            
            Object[] row = {
                booking.getId(),
                courtName,
                booking.getCourtType(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void handleUpdate(ActionEvent e) {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow >= 0) {
            int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
            String status = (String) tableModel.getValueAt(selectedRow, 5);
            
            if (!"SCHEDULED".equals(status)) {
                JOptionPane.showMessageDialog(this, 
                    "Only scheduled bookings can be updated", 
                    "Update Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Show update dialog
            showUpdateDialog(bookingId);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a booking first", 
                "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void showUpdateDialog(int bookingId) {
        // Get the booking
        Booking booking = controller.getBookingById(bookingId);
        if (booking == null) return;
        
        // Create a dialog for updating
        JDialog updateDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Update Booking", true);
        updateDialog.setLayout(new BorderLayout());
        updateDialog.setSize(400, 300);
        updateDialog.setLocationRelativeTo(this);
        
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        
        JLabel courtLabel = new JLabel("Court:");
        JComboBox<Court> courtComboUpdate = new JComboBox<>();
        
        // Populate court combo and select current court
        List<Court> courts = controller.getAllCourts();
        Court selectedCourt = null;
        for (Court court : courts) {
            courtComboUpdate.addItem(court);
            if (court.getId() == booking.getCourtId()) {
                selectedCourt = court;
            }
        }
        if (selectedCourt != null) {
            courtComboUpdate.setSelectedItem(selectedCourt);
        }
        
        JLabel dateLabel = new JLabel("Date (yyyy-MM-dd):");
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(booking.getStartTime()));
        
        JLabel startTimeLabel = new JLabel("Start Time (HH:mm):");
        JTextField startTimeField = new JTextField(new SimpleDateFormat("HH:mm").format(booking.getStartTime()));
        
        JLabel durationLabel = new JLabel("Duration (hours):");
        JComboBox<Integer> durationCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4});
        
        // Calculate current duration in hours
        long durationMillis = booking.getEndTime().getTime() - booking.getStartTime().getTime();
        int durationHours = (int) (durationMillis / (60 * 60 * 1000));
        durationCombo.setSelectedItem(Math.max(1, Math.min(4, durationHours))); // Keep between 1-4
        
        inputPanel.add(courtLabel);
        inputPanel.add(courtComboUpdate);
        inputPanel.add(dateLabel);
        inputPanel.add(dateField);
        inputPanel.add(startTimeLabel);
        inputPanel.add(startTimeField);
        inputPanel.add(durationLabel);
        inputPanel.add(durationCombo);
        
        JButton updateBtn = new JButton("Update Booking");
        
        updateBtn.addActionListener(event -> {
            try {
                // Get selected court
                Court court = (Court) courtComboUpdate.getSelectedItem();
                if (court == null) {
                    JOptionPane.showMessageDialog(updateDialog, 
                        "Please select a court", 
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Parse date and time
                String dateStr = dateField.getText().trim();
                String timeStr = startTimeField.getText().trim();
                if (dateStr.isEmpty() || timeStr.isEmpty()) {
                    JOptionPane.showMessageDialog(updateDialog, 
                        "Please enter date and time", 
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Parse date/time
                Date date = dateFormat.parse(dateStr + " " + timeStr);
                Timestamp startTime = new Timestamp(date.getTime());
                
                // Calculate end time based on duration
                int duration = (int) durationCombo.getSelectedItem();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.HOUR_OF_DAY, duration);
                Timestamp endTime = new Timestamp(calendar.getTimeInMillis());
                
                // Update booking
                booking.setCourtId(court.getId());
                booking.setCourtType(court.getType());
                booking.setStartTime(startTime);
                booking.setEndTime(endTime);
                
                if (controller.updateBooking(booking)) {
                    JOptionPane.showMessageDialog(updateDialog, 
                        "Booking updated successfully", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    updateDialog.dispose();
                    refreshTableData();
                } else {
                    JOptionPane.showMessageDialog(updateDialog, 
                        "Failed to update booking. Time slot may not be available.", 
                        "Update Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(updateDialog, 
                    "Invalid date/time format. Use yyyy-MM-dd for date and HH:mm for time", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        updateDialog.add(inputPanel, BorderLayout.CENTER);
        updateDialog.add(updateBtn, BorderLayout.SOUTH);
        
        updateDialog.setVisible(true);
    }
    
    private void handleCancel(ActionEvent e) {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow >= 0) {
            int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
            String status = (String) tableModel.getValueAt(selectedRow, 5);
            
            if (!"SCHEDULED".equals(status)) {
                JOptionPane.showMessageDialog(this, 
                    "Only scheduled bookings can be cancelled", 
                    "Cancellation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this booking?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                if (controller.cancelBooking(bookingId)) {
                    JOptionPane.showMessageDialog(this, 
                        "Booking cancelled successfully", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshTableData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to cancel booking", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a booking first", 
                "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}
        