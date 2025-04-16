package com.arenaedge.model.equipment;

public class BadmintonEquipment extends Equipment {
    private int stringTension; // in pounds
    private boolean isFeatherShuttlecock;
    
    public BadmintonEquipment() {
        super();
        setType("BADMINTON");
        this.stringTension = 20; // Default tension
        this.isFeatherShuttlecock = true;
    }
    
    public BadmintonEquipment(int id, String name, String status, int conditionRating, 
                             int stringTension, boolean isFeatherShuttlecock) {
        super(id, name, "BADMINTON", status, conditionRating);
        this.stringTension = stringTension;
        this.isFeatherShuttlecock = isFeatherShuttlecock;
    }
    
    @Override
    public boolean validateEquipment() {
        // Badminton specific validation
        return stringTension >= 18 && stringTension <= 24 && getConditionRating() > 2;
    }
    
    @Override
    public String getMaintenanceRequirements() {
        return "Check string tension (ideal: 18-24 lbs). Check shuttlecock feathers or plastic.";
    }
    
    // Getters and setters
    public int getStringTension() {
        return stringTension;
    }
    
    public void setStringTension(int stringTension) {
        this.stringTension = stringTension;
    }
    
    public boolean isFeatherShuttlecock() {
        return isFeatherShuttlecock;
    }
    
    public void setFeatherShuttlecock(boolean featherShuttlecock) {
        isFeatherShuttlecock = featherShuttlecock;
    }
}