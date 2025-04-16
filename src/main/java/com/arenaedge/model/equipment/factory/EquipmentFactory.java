package com.arenaedge.model.equipment.factory;

import com.arenaedge.model.equipment.*;

// Factory Pattern Implementation
public class EquipmentFactory {
    
    public Equipment createEquipment(String type) {
        if (type == null) {
            return null;
        }
        
        switch (type.toUpperCase()) {
            case "BASKETBALL":
                return new BasketballEquipment();
            case "TENNIS":
                return new TennisEquipment();
            case "BADMINTON":
                return new BadmintonEquipment();
            default:
                throw new IllegalArgumentException("Unknown equipment type: " + type);
        }
    }
    
    // Overloaded method for creating equipment with specific properties
    public Equipment createEquipment(String type, int id, String name, String status, int conditionRating) {
        Equipment equipment = createEquipment(type);
        equipment.setId(id);
        equipment.setName(name);
        equipment.setStatus(status);
        equipment.setConditionRating(conditionRating);
        return equipment;
    }
    
    // Factory method to create equipment from database values
    public Equipment createEquipmentFromDatabase(int id, String name, String type, 
                                                String status, int conditionRating) {
        return createEquipment(type, id, name, status, conditionRating);
    }
}