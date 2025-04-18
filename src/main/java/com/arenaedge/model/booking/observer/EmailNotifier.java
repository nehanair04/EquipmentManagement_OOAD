package com.arenaedge.model.booking.observer;

import com.arenaedge.model.booking.Booking;
import com.arenaedge.util.EmailService;

public class EmailNotifier implements BookingObserver {
    private final EmailService emailService;
    
    public EmailNotifier() {
        this.emailService = new EmailService();
    }
    
    @Override
    public void update(Booking booking, String eventType) {
        // In a real app, we would get the actual user email from a user service
        String userEmail = "user" + booking.getUserId() + "@gmail.com";
        
        // Create appropriate email subject and body based on event type
        String subject = "";
        String body = "";
        
        switch (eventType) {
            case "CREATED":
                subject = "Booking Confirmation - ArenaEdge";
                body = "Your booking has been confirmed!\n\n" +
                       "Court: #" + booking.getCourtId() + " (" + booking.getCourtType() + ")\n" +
                       "Start Time: " + booking.getStartTime() + "\n" +
                       "End Time: " + booking.getEndTime() + "\n\n" +
                       "Thank you for using ArenaEdge!";
                break;
            case "UPDATED":
                subject = "Booking Update - ArenaEdge";
                body = "Your booking has been updated.\n\n" +
                       "Court: #" + booking.getCourtId() + " (" + booking.getCourtType() + ")\n" +
                       "Start Time: " + booking.getStartTime() + "\n" +
                       "End Time: " + booking.getEndTime() + "\n\n" +
                       "Thank you for using ArenaEdge!";
                break;
            case "CANCELLED":
                subject = "Booking Cancellation - ArenaEdge";
                body = "Your booking has been cancelled.\n\n" +
                       "Court: #" + booking.getCourtId() + " (" + booking.getCourtType() + ")\n" +
                       "Start Time: " + booking.getStartTime() + "\n" +
                       "End Time: " + booking.getEndTime() + "\n\n" +
                       "Thank you for using ArenaEdge!";
                break;
            case "REMINDER":
                subject = "Booking Reminder - ArenaEdge";
                body = "Reminder: You have an upcoming booking!\n\n" +
                       "Court: #" + booking.getCourtId() + " (" + booking.getCourtType() + ")\n" +
                       "Start Time: " + booking.getStartTime() + "\n" +
                       "End Time: " + booking.getEndTime() + "\n\n" +
                       "We look forward to seeing you soon!";
                break;
        }
        
        // Send the email
        boolean sent = emailService.sendEmail(userEmail, subject, body);
        
        // Log status
        System.out.println("EMAIL NOTIFICATION: " + eventType + " - " + booking);
        System.out.println("Email to " + userEmail + " " + (sent ? "sent successfully" : "failed to send"));
    }
}