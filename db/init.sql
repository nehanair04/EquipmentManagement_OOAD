-- Create database
CREATE DATABASE IF NOT EXISTS arenaedge;
USE arenaedge;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Equipment table
CREATE TABLE IF NOT EXISTS equipment (
    equipment_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    status ENUM('AVAILABLE', 'CHECKED_OUT', 'MAINTENANCE') DEFAULT 'AVAILABLE',
    condition_rating INT DEFAULT 5,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_checked_out TIMESTAMP NULL
);

-- Equipment checkout log
CREATE TABLE IF NOT EXISTS equipment_checkout (
    checkout_id INT AUTO_INCREMENT PRIMARY KEY,
    equipment_id INT NOT NULL,
    user_id INT NOT NULL,
    checkout_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    return_time TIMESTAMP NULL,
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Equipment feedback
CREATE TABLE IF NOT EXISTS equipment_feedback (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    equipment_id INT NOT NULL,
    user_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comments TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Insert some sample data
INSERT INTO users (username, password, name, email, phone)
VALUES
('pes311', '311@pes', 'mihir', 'mihir@pes', '1234567890'),
('pes336', '336@pes', 'nandana', 'nandana@pes', '9876543210'),
('pes342', '342@pes', 'natasha', 'natasha@pes', '5555555555'),
('pes348', '348@pes', 'neha', 'neha@pes','1111111111'), 
('user1', 'password123', 'John Doe', 'john@example.com', '555-1234'),
('user2', 'password123', 'Jane Smith', 'jane@example.com', '555-5678');

INSERT INTO equipment (name, type, status)
VALUES 
('Basketball #1', 'BASKETBALL', 'AVAILABLE'),
('Basketball #2', 'BASKETBALL', 'AVAILABLE'),
('Tennis Racket #1', 'TENNIS', 'AVAILABLE'),
('Tennis Racket #2', 'TENNIS', 'MAINTENANCE'),
('Badminton Racket #1', 'BADMINTON', 'AVAILABLE'),
('Badminton Racket #2', 'BADMINTON', 'CHECKED_OUT');


-- Courts table
CREATE TABLE IF NOT EXISTS courts (
    court_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bookings table
CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    court_id INT NOT NULL,
    court_type VARCHAR(50) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'SCHEDULED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (court_id) REFERENCES courts(court_id)
);

-- Insert some sample courts
INSERT INTO courts (name, type, available) VALUES 
('Badminton Court #1', 'BADMINTON', TRUE),
('Badminton Court #2', 'BADMINTON', TRUE),
('Squash Court #1', 'SQUASH', TRUE),
('Squash Court #2', 'SQUASH', TRUE),
('Tennis Court #1', 'TENNIS', TRUE),
('Tennis Court #2', 'TENNIS', TRUE);

CREATE TABLE IF NOT EXISTS gym_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    timestamp DATETIME NOT NULL,
    log_type VARCHAR(10) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Memberships table
CREATE TABLE IF NOT EXISTS memberships (
    membership_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    fee DOUBLE NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    membership_id INT DEFAULT 0,
    booking_id INT DEFAULT 0,
    amount DOUBLE NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    transaction_id VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- User notification settings table
CREATE TABLE IF NOT EXISTS user_notification_settings (
    user_id INT PRIMARY KEY,
    email_notifications BOOLEAN DEFAULT TRUE,
    sms_notifications BOOLEAN DEFAULT FALSE,
    booking_reminders BOOLEAN DEFAULT TRUE,
    payment_reminders BOOLEAN DEFAULT TRUE,
    promotional_notifications BOOLEAN DEFAULT FALSE,
    reminder_hours INT DEFAULT 24,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Insert sample memberships
INSERT INTO memberships (user_id, type, start_date, end_date, fee, active, payment_status) VALUES
(1, 'BASIC', DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 2 MONTH), DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 10 MONTH), 49.99, true, 'PAID'),
(2, 'PREMIUM', DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 MONTH), DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 11 MONTH), 79.99, true, 'PAID'),
(3, 'GOLD', CURRENT_TIMESTAMP, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 12 MONTH), 99.99, true, 'PENDING'),
(4, 'BASIC', DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 3 MONTH), DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 DAY), 49.99, false, 'PAID');

-- Insert sample payments
INSERT INTO payments (user_id, membership_id, amount, payment_method, status, transaction_id) VALUES
(1, 1, 49.99, 'CREDIT_CARD', 'COMPLETED', 'TXN123456789'),
(2, 2, 79.99, 'DEBIT_CARD', 'COMPLETED', 'TXN987654321'),
(3, 0, 25.00, 'CASH', 'COMPLETED', 'TXN202304150001');

-- Insert sample notification settings
INSERT INTO user_notification_settings (user_id, email_notifications, sms_notifications, booking_reminders, payment_reminders, promotional_notifications, reminder_hours) VALUES
(1, true, false, true, true, false, 24),
(2, true, true, true, true, true, 12),
(3, false, true, true, false, false, 48);
