package com.arenaedge.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // For MySQL
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/arenaedge";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "password"; // Change this
    
    private static Connection connection = null;
    
    public static Connection getConnection() {
        try {
            boolean renew = false;
            if (connection != null) {
                try {
                    renew = !connection.isValid(2); // Check validity with 2-second timeout
                } catch (SQLException e) {
                    renew = true; // Assume invalid if exception occurs
                }
            }
            if (connection == null || renew) {
                // Close existing connection if possible
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        // Ignore exception during close
                    }
                }
                // Create new connection
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.err.println("Database connection failed: " + e.getMessage());
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                connection = null; // Ensure connection is set to null after closing
            }
        }
    }
}
