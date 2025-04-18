package com.arenaedge.model.booking.observer;

import com.arenaedge.model.booking.Booking;

// Observer interface for the Observer Pattern
public interface BookingObserver {
    void update(Booking booking, String eventType);
}