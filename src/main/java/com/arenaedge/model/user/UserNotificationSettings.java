package com.arenaedge.model.user;

/**
 * User notification preferences.
 * Part of the user profile customization feature.
 * Follows Information Expert principle by encapsulating notification preferences.
 */
public class UserNotificationSettings {
    private int userId;
    private boolean emailNotifications;
    private boolean smsNotifications;
    private boolean bookingReminders;
    private boolean paymentReminders;
    private boolean promotionalNotifications;
    private int reminderHours; // How many hours before a booking to send reminders
    
    /**
     * Default constructor with default settings
     */
    public UserNotificationSettings() {
        this.emailNotifications = true;
        this.smsNotifications = false;
        this.bookingReminders = true;
        this.paymentReminders = true;
        this.promotionalNotifications = false;
        this.reminderHours = 24;
    }
    
    /**
     * Constructor with user ID
     */
    public UserNotificationSettings(int userId) {
        this();
        this.userId = userId;
    }
    
    /**
     * Enable all notifications
     */
    public void enableAll() {
        this.emailNotifications = true;
        this.smsNotifications = true;
        this.bookingReminders = true;
        this.paymentReminders = true;
        this.promotionalNotifications = true;
    }
    
    /**
     * Disable all notifications
     */
    public void disableAll() {
        this.emailNotifications = false;
        this.smsNotifications = false;
        this.bookingReminders = false;
        this.paymentReminders = false;
        this.promotionalNotifications = false;
    }
    
    /**
     * Check if any notification method is enabled
     * 
     * @return true if either email or SMS is enabled
     */
    public boolean hasNotificationMethod() {
        return emailNotifications || smsNotifications;
    }
    
    /**
     * Check if a specific notification type should be sent
     * 
     * @param notificationType type of notification ("BOOKING", "PAYMENT", "PROMO")
     * @return true if user should receive the notification
     */
    public boolean shouldNotify(String notificationType) {
        if (!hasNotificationMethod()) {
            return false;
        }
        
        switch (notificationType) {
            case "BOOKING":
                return bookingReminders;
            case "PAYMENT":
                return paymentReminders;
            case "PROMO":
                return promotionalNotifications;
            default:
                return false;
        }
    }

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public boolean isSmsNotifications() {
        return smsNotifications;
    }

    public void setSmsNotifications(boolean smsNotifications) {
        this.smsNotifications = smsNotifications;
    }

    public boolean isBookingReminders() {
        return bookingReminders;
    }

    public void setBookingReminders(boolean bookingReminders) {
        this.bookingReminders = bookingReminders;
    }

    public boolean isPaymentReminders() {
        return paymentReminders;
    }

    public void setPaymentReminders(boolean paymentReminders) {
        this.paymentReminders = paymentReminders;
    }

    public boolean isPromotionalNotifications() {
        return promotionalNotifications;
    }

    public void setPromotionalNotifications(boolean promotionalNotifications) {
        this.promotionalNotifications = promotionalNotifications;
    }

    public int getReminderHours() {
        return reminderHours;
    }

    public void setReminderHours(int reminderHours) {
        this.reminderHours = reminderHours;
    }
}
