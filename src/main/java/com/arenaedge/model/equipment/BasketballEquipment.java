package com.arenaedge.model.equipment;

public class BasketballEquipment extends Equipment {
    private double airPressure; // in PSI
    
    public BasketballEquipment() {
        super();
        setType("BASKETBALL");
        this.airPressure = 8.0; // Default pressure
    }
    
    public BasketballEquipment(int id, String name, String status, int conditionRating, double airPressure) {
        super(id, name, "BASKETBALL", status, conditionRating);
        this.airPressure = airPressure;
    }
    
    @Override
    public boolean validateEquipment() {
        // Basketball specific validation
        return airPressure >= 7.5 && airPressure <= 8.5 && getConditionRating() > 2;
    }
    
    @Override
    public String getMaintenanceRequirements() {
        return "Check air pressure (ideal: 7.5-8.5 PSI). Inspect for smooth surface.";
    }
    
    // Getters and setters
    public double getAirPressure() {
        return airPressure;
    }
    
    public void setAirPressure(double airPressure) {
        this.airPressure = airPressure;
    }
}