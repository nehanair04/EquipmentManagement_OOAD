package com.arenaedge.model.equipment;

public class TennisEquipment extends Equipment {
    private int stringTension; // in pounds
    
    public TennisEquipment() {
        super();
        setType("TENNIS");
        this.stringTension = 55; // Default tension
    }
    
    public TennisEquipment(int id, String name, String status, int conditionRating, int stringTension) {
        super(id, name, "TENNIS", status, conditionRating);
        this.stringTension = stringTension;
    }
    
    @Override
    public boolean validateEquipment() {
        // Tennis equipment specific validation
        return stringTension >= 40 && stringTension <= 65 && getConditionRating() > 2;
    }
    
    @Override
    public String getMaintenanceRequirements() {
        return "Check string tension (ideal: 40-65 lbs). Inspect grip and frame for damage.";
    }
    
    // Getters and setters
    public int getStringTension() {
        return stringTension;
    }
    
    public void setStringTension(int stringTension) {
        this.stringTension = stringTension;
    }
}