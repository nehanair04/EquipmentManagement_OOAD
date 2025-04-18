package com.arenaedge.controller;

import com.arenaedge.model.booking.Booking;
import com.arenaedge.model.booking.Court;
import com.arenaedge.model.booking.observer.BookingObserver;
import com.arenaedge.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Observer pattern implementation - this is the Observable/Subject
public class BookingController {
    private List<BookingObserver> observers;
    
    public BookingController() {
        this.observers = new ArrayList<>();
    }
    
    // Observer pattern methods
    public void addObserver(BookingObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(BookingObserver observer) {
        observers.remove(observer);
    }
    
    // Notify all observers of a booking event
    private void notifyObservers(Booking booking, String eventType) {
        for (BookingObserver observer : observers) {
            observer.update(booking, eventType);
        }
    }
    
    // Get all courts
    public List<Court> getAllCourts() {
        List<Court> courtList = new ArrayList<>();
        String query = "SELECT * FROM courts";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Court court = new Court();
                court.setId(rs.getInt("court_id"));
                court.setName(rs.getString("name"));
                court.setType(rs.getString("type"));
                court.setAvailable(rs.getBoolean("available"));
                
                courtList.add(court);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return courtList;
    }
    
    // Get court by ID
    public Court getCourtById(int id) {
        String query = "SELECT * FROM courts WHERE court_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Court court = new Court();
                court.setId(rs.getInt("court_id"));
                court.setName(rs.getString("name"));
                court.setType(rs.getString("type"));
                court.setAvailable(rs.getBoolean("available"));
                
                return court;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Get all bookings
    public List<Booking> getAllBookings() {
        List<Booking> bookingList = new ArrayList<>();
        String query = "SELECT * FROM bookings ORDER BY start_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt("booking_id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setCourtId(rs.getInt("court_id"));
                booking.setStartTime(rs.getTimestamp("start_time"));
                booking.setEndTime(rs.getTimestamp("end_time"));
                booking.setStatus(rs.getString("status"));
                booking.setCourtType(rs.getString("court_type"));
                
                bookingList.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bookingList;
    }
    
    // Get bookings for a specific user
    public List<Booking> getBookingsForUser(int userId) {
        List<Booking> bookingList = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE user_id = ? ORDER BY start_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt("booking_id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setCourtId(rs.getInt("court_id"));
                booking.setStartTime(rs.getTimestamp("start_time"));
                booking.setEndTime(rs.getTimestamp("end_time"));
                booking.setStatus(rs.getString("status"));
                booking.setCourtType(rs.getString("court_type"));
                
                bookingList.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bookingList;
    }
    
    // Create a new booking
    public boolean createBooking(Booking booking) {
        // First check if there's a conflict with existing bookings
        if (!isTimeSlotAvailable(booking.getCourtId(), booking.getStartTime(), booking.getEndTime())) {
            return false;
        }
        
        String query = "INSERT INTO bookings (user_id, court_id, start_time, end_time, status, court_type) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getCourtId());
            stmt.setTimestamp(3, booking.getStartTime());
            stmt.setTimestamp(4, booking.getEndTime());
            stmt.setString(5, booking.getStatus());
            stmt.setString(6, booking.getCourtType());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    booking.setId(generatedKeys.getInt(1));
                }
                
                // Notify observers about the new booking
                notifyObservers(booking, "CREATED");
                
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Update an existing booking
    public boolean updateBooking(Booking booking) {
        // First check if there's a conflict with existing bookings (excluding this booking)
        if (!isTimeSlotAvailable(booking.getCourtId(), booking.getStartTime(), booking.getEndTime(), booking.getId())) {
            return false;
        }
        
        String query = "UPDATE bookings SET user_id = ?, court_id = ?, start_time = ?, end_time = ?, status = ?, court_type = ? WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getCourtId());
            stmt.setTimestamp(3, booking.getStartTime());
            stmt.setTimestamp(4, booking.getEndTime());
            stmt.setString(5, booking.getStatus());
            stmt.setString(6, booking.getCourtType());
            stmt.setInt(7, booking.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Notify observers about the updated booking
                notifyObservers(booking, "UPDATED");
                
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Cancel a booking
    public boolean cancelBooking(int bookingId) {
        String query = "UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, bookingId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the updated booking
                Booking booking = getBookingById(bookingId);
                
                // Notify observers about the cancelled booking
                notifyObservers(booking, "CANCELLED");
                
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Get booking by ID
    public Booking getBookingById(int id) {
        String query = "SELECT * FROM bookings WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt("booking_id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setCourtId(rs.getInt("court_id"));
                booking.setStartTime(rs.getTimestamp("start_time"));
                booking.setEndTime(rs.getTimestamp("end_time"));
                booking.setStatus(rs.getString("status"));
                booking.setCourtType(rs.getString("court_type"));
                
                return booking;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Check if a time slot is available for a court
    private boolean isTimeSlotAvailable(int courtId, Timestamp startTime, Timestamp endTime) {
        return isTimeSlotAvailable(courtId, startTime, endTime, -1);
    }
    
    // Check if a time slot is available for a court (excluding a specific booking)
    private boolean isTimeSlotAvailable(int courtId, Timestamp startTime, Timestamp endTime, int excludeBookingId) {
        String query = "SELECT COUNT(*) FROM bookings WHERE court_id = ? AND status = 'SCHEDULED' " +
                      "AND ((start_time <= ? AND end_time > ?) OR (start_time < ? AND end_time >= ?) OR (start_time >= ? AND end_time <= ?))";
        
        if (excludeBookingId > 0) {
            query += " AND booking_id != ?";
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, courtId);
            stmt.setTimestamp(2, startTime);
            stmt.setTimestamp(3, startTime);
            stmt.setTimestamp(4, endTime);
            stmt.setTimestamp(5, endTime);
            stmt.setTimestamp(6, startTime);
            stmt.setTimestamp(7, endTime);
            
            if (excludeBookingId > 0) {
                stmt.setInt(8, excludeBookingId);
            }
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                return count == 0; // If count is 0, the time slot is available
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Send reminders for upcoming bookings
    public void sendReminders() {
        // Get bookings that are starting within the next hour
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp oneHourLater = new Timestamp(System.currentTimeMillis() + 3600000); // + 1 hour in milliseconds
        
        String query = "SELECT * FROM bookings WHERE status = 'SCHEDULED' AND start_time BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setTimestamp(1, now);
            stmt.setTimestamp(2, oneHourLater);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt("booking_id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setCourtId(rs.getInt("court_id"));
                booking.setStartTime(rs.getTimestamp("start_time"));
                booking.setEndTime(rs.getTimestamp("end_time"));
                booking.setStatus(rs.getString("status"));
                booking.setCourtType(rs.getString("court_type"));
                
                // Notify observers to send reminders
                notifyObservers(booking, "REMINDER");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}