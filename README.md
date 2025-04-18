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
│   └── init.sql                                     # Database initialization script
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── arenaedge/
│   │   │           ├── Main.java                    # Application entry point
│   │   │           ├── controller/
│   │   │           │   └── EquipmentController.java # Business logic for equipment
│   │   │           │   └── BookingController.java   # Business logic for booking
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
│   │   │           │   ├── booking/                        # Team Member 2
│   │   │           │   │   ├── Booking.java
│   │   │           │   │   ├── Court.java
│   │   │           │   │   └── observer/
│   │   │           │   │       ├── BookingObserver.java     # Observer interface
│   │   │           │   │       ├── EmailNotifier.java       # Concrete observer
│   │   │           │   │       └── SMSNotifier.java         # Concrete observer
│   │   │           │   ├── gym/                            # For Team Member 3
│   │   │           │   │   └── GymLog.java                 # Placeholder
│   │   │           │   ├── membership/                     # For Team Member 4
│   │   │           │   │   └── Membership.java             # Placeholder
│   │   │           │   └── user/
│   │   │           │       └── User.java                   # User model (for authentication)
│   │   │           ├── util/
│   │   │           │   └── DatabaseConnection.java         # Database utility
│   │   │           │   ├── EmailService.java               # Email sender utility
│   │   │           │   └── GreenMailManager.java           # For testing emails
│   │   │           └── view/
│   │   │               ├── MainFrame.java                  # Main application window
│   │   │               ├── equipment/
│   │   │               │   ├── EquipmentPanel.java         # UI for equipment management
│   │   │               │   └── EquipmentFeedbackPanel.java # UI for feedback
│   │   │               ├── booking/                        # Team Member 2
│   │   │               │   └── BookingPanel.java           # UI for booking
│   │   │               ├── gym/                            # For Team Member 3
│   │   │               │   └── GymLogPanel.java            # Placeholder
│   │   │               └── membership/                     # For Team Member 4
│   │   │                   └── MembershipPanel.java        # Placeholder
│   │   └── resources/                                      # For configuration files, etc.
│   └── test/                                               # For test classes
│       └── java/
│           └── com/
│               └── arenaedge/
│                   └── ... (test classes)
└── target/                                          # Maven build output
    └── ... I RAN OUT OF CREDITS BUT YOU GET THE IDEA
