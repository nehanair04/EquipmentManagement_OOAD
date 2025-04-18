# EquipmentManagement_OOAD

### Install and Setup Environment Variables - Apache Maven

### Run Instructions (Within "project" directory)
```bash
mvn clean compile
mvn exec:java
```
### directory structure
```bash
ArenaEdge/
├── pom.xml                                          # Maven configuration file
├── db/
│   └── init.sql                                     # Database initialization script with gym_logs table
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── arenaedge/
│   │   │           ├── Main.java                    # Application entry point
│   │   │           ├── controller/
│   │   │           │   ├── EquipmentController.java # Business logic for equipment
│   │   │           │   ├── BookingController.java   # Business logic for booking
│   │   │           │   └── GymController.java       # Business logic for gym access
│   │   │           ├── model/
│   │   │           │   ├── equipment/
│   │   │           │   │   ├── Equipment.java              # Base class (SRP)
│   │   │           │   │   ├── BasketballEquipment.java    # Basketball type
│   │   │           │   │   ├── TennisEquipment.java        # Tennis type
│   │   │           │   │   ├── BadmintonEquipment.java     # Badminton type
│   │   │           │   │   └── factory/
│   │   │           │   │       └── EquipmentFactory.java   # Factory Pattern implementation
│   │   │           │   ├── feedback/
│   │   │           │   │   └── EquipmentFeedback.java      # Feedback model (SRP)
│   │   │           │   ├── booking/
│   │   │           │   │   ├── Booking.java
│   │   │           │   │   ├── Court.java
│   │   │           │   │   └── observer/
│   │   │           │   │       ├── BookingObserver.java    # Observer interface
│   │   │           │   │       ├── EmailNotifier.java      # Concrete observer
│   │   │           │   │       └── SMSNotifier.java        # Concrete observer
│   │   │           │   ├── gym/
│   │   │           │   │   ├── GymLog.java                 # Gym log entity
│   │   │           │   │   └── LogType.java                # Enum for log types
│   │   │           │   ├── membership/
│   │   │           │   │   └── Membership.java             # Placeholder
│   │   │           │   └── user/
│   │   │           │       └── User.java                   # User model (updated to match DB schema)
│   │   │           ├── command/
│   │   │           │   └── gym/
│   │   │           │       ├── GymCommand.java             # Command interface
│   │   │           │       ├── GymEntryCommand.java        # Entry command
│   │   │           │       ├── GymExitCommand.java         # Exit command
│   │   │           │       └── GymAccessController.java    # Command invoker
│   │   │           ├── dao/
│   │   │           │   ├── GymLogDAO.java                  # Interface for gym log data access
│   │   │           │   └── impl/
│   │   │           │       └── GymLogDAOImpl.java          # Implementation of GymLogDAO
│   │   │           ├── util/
│   │   │           │   ├── DatabaseConnection.java         # Database utility
│   │   │           │   ├── EmailService.java               # Email sender utility
│   │   │           │   └── GreenMailManager.java           # For testing emails
│   │   │           └── view/
│   │   │               ├── MainFrame.java                  # Main application window (updated)
│   │   │               ├── equipment/
│   │   │               │   ├── EquipmentPanel.java         # UI for equipment management
│   │   │               │   └── EquipmentFeedbackPanel.java # UI for feedback
│   │   │               ├── booking/
│   │   │               │   └── BookingPanel.java           # UI for booking
│   │   │               ├── gym/
│   │   │               │   ├── GymLogPanel.java            # UI for gym access logging
│   │   │               │   └── GymAdminDashboardPanel.java # UI for gym analytics
│   │   │               └── membership/
│   │   │                   └── MembershipPanel.java        # Placeholder
│   │   └── resources/                                      # For configuration files, etc.
└── target/                                          # Maven build output
    └── ...
