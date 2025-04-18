package com.arenaedge.view.gym;

import com.arenaedge.controller.GymController;
import com.arenaedge.model.gym.GymLog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

/**
 * Admin dashboard for gym usage statistics.
 * Provides insights into gym activity.
 */
public class GymAdminDashboardPanel extends JPanel {
    private JComboBox<String> timeRangeSelector;
    private JPanel chartPanel;
    private JTable statsTable;
    private DefaultTableModel tableModel;
    
    private GymController controller;
    
    /**
     * Constructor for GymAdminDashboardPanel
     */
    public GymAdminDashboardPanel() {
        controller = new GymController();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Initialize components
        initializeComponents();
        
        // Add action listeners
        setupActionListeners();
        
        // Initial data load
        refreshDashboard("Today");
    }
    
    /**
     * Initialize UI components
     */
    private void initializeComponents() {
        // Top panel with filters
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel filterLabel = new JLabel("Time Range:");
        String[] timeRanges = {"Today", "Yesterday", "This Week", "This Month", "Custom"};
        timeRangeSelector = new JComboBox<>(timeRanges);
        
        topPanel.add(filterLabel);
        topPanel.add(timeRangeSelector);
        
        // Chart panel for visualizations
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("Gym Usage by Hour"));
        
        // Table for statistics
        String[] columns = {"Metric", "Value"};
        tableModel = new DefaultTableModel(columns, 0);
        statsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(statsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Gym Usage Statistics"));
        scrollPane.setPreferredSize(new Dimension(300, 200));
        
        // Layout components
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.add(chartPanel);
        centerPanel.add(scrollPane);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    /**
     * Set up action listeners
     */
    private void setupActionListeners() {
        timeRangeSelector.addActionListener(e -> {
            String selectedRange = (String) timeRangeSelector.getSelectedItem();
            refreshDashboard(selectedRange);
        });
    }
    
    /**
     * Refresh the dashboard with data for the selected time range
     * 
     * @param timeRange the selected time range
     */
    private void refreshDashboard(String timeRange) {
        // Get date range based on selected time range
        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();
        
        switch (timeRange) {
            case "Today":
                startDate = LocalDate.now().atStartOfDay();
                break;
            case "Yesterday":
                startDate = LocalDate.now().minusDays(1).atStartOfDay();
                endDate = LocalDate.now().atStartOfDay();
                break;
            case "This Week":
                startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
                break;
            case "This Month":
                startDate = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                break;
            case "Custom":
                // In a real app, we would show a date picker
                // For now, just use last 30 days
                startDate = LocalDate.now().minusDays(30).atStartOfDay();
                JOptionPane.showMessageDialog(this, 
                    "Custom date selection would appear here. Using last 30 days.", 
                    "Date Selection", JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                startDate = LocalDate.now().atStartOfDay();
        }
        
        // Load data for the selected range
        loadChartData(startDate, endDate);
        loadStatistics(startDate, endDate);
    }
    
    /**
     * Load chart data for the specified date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     */
    private void loadChartData(LocalDateTime startDate, LocalDateTime endDate) {
        // Get hourly usage data from controller
        Map<Integer, Integer> hourlyUsage = controller.calculateHourlyUsage(startDate, endDate);
        
        // Clear the chart panel
        chartPanel.removeAll();
        
        // Create a simple bar chart using Swing components
        JPanel barChart = new JPanel(new BorderLayout());
        JPanel barsPanel = new JPanel(new GridLayout(1, 24, 2, 0));
        
        // Find the maximum value for scaling
        int maxValue = hourlyUsage.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        
        // Create bars for each hour
        for (int hour = 0; hour < 24; hour++) {
            int value = hourlyUsage.getOrDefault(hour, 0);
            
            // Calculate bar height as a percentage of the maximum
            double ratio = maxValue > 0 ? (double) value / maxValue : 0;
            int height = (int) (150 * ratio);
            
            // Create a panel for this hour
            JPanel hourPanel = new JPanel(new BorderLayout());
            
            // Create the bar
            JPanel bar = new JPanel();
            bar.setBackground(new Color(41, 128, 185)); // Blue color
            bar.setPreferredSize(new Dimension(15, height));
            
            // Add some padding at the top
            JPanel barContainer = new JPanel(new BorderLayout());
            barContainer.add(new JPanel(), BorderLayout.CENTER);
            barContainer.add(bar, BorderLayout.SOUTH);
            
            // Add the hour label
            JLabel hourLabel = new JLabel(String.valueOf(hour));
            hourLabel.setHorizontalAlignment(SwingConstants.CENTER);
            hourLabel.setFont(new Font("SansSerif", Font.PLAIN, 9));
            
            // Add components to the hour panel
            hourPanel.add(barContainer, BorderLayout.CENTER);
            hourPanel.add(hourLabel, BorderLayout.SOUTH);
            
            // Add to the bars panel
            barsPanel.add(hourPanel);
        }
        
        // Add labels
        JLabel titleLabel = new JLabel("Number of Entries by Hour of Day", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        // Add components to the bar chart
        barChart.add(titleLabel, BorderLayout.NORTH);
        barChart.add(barsPanel, BorderLayout.CENTER);
        
        // Add to the chart panel
        chartPanel.add(barChart, BorderLayout.CENTER);
        
        // Refresh the panel
        chartPanel.revalidate();
        chartPanel.repaint();
    }
    
    /**
     * Load statistics for the specified date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     */
    private void loadStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get data from controller
        List<GymLog> logs = controller.getLogsByDateRange(startDate, endDate);
        
        // Calculate statistics
        
        // 1. Total visits (entries)
        long totalEntries = logs.stream()
                .filter(log -> log.getType().name().equals("ENTRY"))
                .count();
        tableModel.addRow(new Object[]{"Total Visits", totalEntries});
        
        // 2. Unique visitors
        long uniqueVisitors = logs.stream()
                .filter(log -> log.getType().name().equals("ENTRY"))
                .map(GymLog::getUserId)
                .distinct()
                .count();
        tableModel.addRow(new Object[]{"Unique Visitors", uniqueVisitors});
        
        // 3. Peak hour
        Map<Integer, Integer> hourlyUsage = controller.calculateHourlyUsage(startDate, endDate);
        int peakHour = 0;
        int peakCount = 0;
        
        for (Map.Entry<Integer, Integer> entry : hourlyUsage.entrySet()) {
            if (entry.getValue() > peakCount) {
                peakHour = entry.getKey();
                peakCount = entry.getValue();
            }
        }
        
        tableModel.addRow(new Object[]{"Peak Hour", peakHour + ":00 (" + peakCount + " visits)"});
        
        // 4. Average time spent
        long avgTime = controller.calculateAverageGymTime(startDate, endDate);
        tableModel.addRow(new Object[]{"Average Time Spent", avgTime + " minutes"});
        
        // 5. Date range
        String dateRangeText = startDate.toLocalDate() + " to " + endDate.toLocalDate();
        tableModel.addRow(new Object[]{"Date Range", dateRangeText});
    }
}