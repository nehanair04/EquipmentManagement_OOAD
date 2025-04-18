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