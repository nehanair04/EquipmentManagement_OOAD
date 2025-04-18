package com.arenaedge;

import com.arenaedge.util.GreenMailManager;
import com.arenaedge.view.MainFrame;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {
        // Start GreenMail server
        GreenMailManager.getInstance().start();
        
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            
            // Add shutdown hook to stop GreenMail when the application closes
            mainFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent windowEvent) {
                    GreenMailManager.getInstance().stop();
                }
            });
            
            mainFrame.setVisible(true);
        });
    }
}