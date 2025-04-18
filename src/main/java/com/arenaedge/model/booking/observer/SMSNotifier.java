package com.arenaedge.model.booking.observer;

import com.arenaedge.model.booking.Booking;

// Another concrete observer implementation
public class SMSNotifier implements BookingObserver {
    @Override
    public void update(Booking booking, String eventType) {
        // In a real app, this would send an actual SMS
        System.out.println("SMS NOTIFICATION: " + eventType + " - " + booking);
        
        switch (eventType) {
            case "CREATED":
                System.out.println("Sending booking confirmation SMS for Court #" + 
                                  booking.getCourtId() + " to User #" + booking.getUserId());
                break;
            case "UPDATED":
                System.out.println("Sending booking update SMS for Court #" + 
                                  booking.getCourtId() + " to User #" + booking.getUserId());
                break;
            case "CANCELLED":
                System.out.println("Sending booking cancellation SMS for Court #" + 
                                  booking.getCourtId() + " to User #" + booking.getUserId());
                break;
            case "REMINDER":
                System.out.println("Sending booking reminder SMS for Court #" + 
                                  booking.getCourtId() + " to User #" + booking.getUserId());
                break;
        }
    }
}