package com.arenaedge.view;

import com.arenaedge.view.booking.BookingPanel;
import com.arenaedge.view.equipment.EquipmentPanel;
import com.arenaedge.view.gym.GymLogPanel;
import com.arenaedge.view.gym.GymAdminDashboardPanel;

import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame {
    
    public MainFrame() {
        // Set up the main frame
        setTitle("ArenaEdge Sports Facility Management");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set up components
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add tabs for each feature
        tabbedPane.addTab("Equipment Management", new EquipmentPanel());
        tabbedPane.addTab("Court Booking", new BookingPanel());
        // Placeholders for other team members' panels
          // Team Member 2
        tabbedPane.addTab("Gym Access", new GymLogPanel());
        tabbedPane.addTab("Gym Analytics", new GymAdminDashboardPanel());

        tabbedPane.addTab("Membership", new JPanel());     // Team Member 4
        
        // Add to frame
        add(tabbedPane, BorderLayout.CENTER);
    }
}