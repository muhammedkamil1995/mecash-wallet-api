-- Create Database
CREATE DATABASE IF NOT EXISTS mecash;
USE mecash;

-- Disable foreign key checks to avoid constraint issues during recreation
SET FOREIGN_KEY_CHECKS = 0;

-- Drop existing tables if they exist
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS wallets;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS currencies;
DROP TABLE IF EXISTS users;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,  -- Store hashed passwords
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Roles Table
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- User Roles Table (Many-to-Many Relationship)
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Currencies Table
CREATE TABLE currencies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    currency_code VARCHAR(10) NOT NULL UNIQUE,
    exchange_rate DECIMAL(10,6) NOT NULL DEFAULT 1.000000, -- Higher precision for rates
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Wallets Table
CREATE TABLE wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00 CHECK (balance >= 0), -- Prevents negative balances
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (currency) REFERENCES currencies(currency_code) ON DELETE CASCADE
);

-- Transactions Table
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_id BIGINT NOT NULL,
    type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER') NOT NULL, 
    amount DECIMAL(15,2) NOT NULL CHECK (amount > 0), -- Prevents negative transactions
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    recipient_wallet_id BIGINT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_wallet_id) REFERENCES wallets(id) ON DELETE SET NULL,
    FOREIGN KEY (currency) REFERENCES currencies(currency_code) ON DELETE CASCADE
);

-- Insert Users (Passwords should be securely hashed in real applications)
INSERT INTO users (username, email, password) 
VALUES 
('kamzee1995', 'kamzycoded@gmail.com', '$2y$10$abcdef1234567890hashedPassword1'), 
('kamil1995', 'kamzeemazeratti@gmail.com', '$2y$10$abcdef1234567890hashedPassword2');

-- Insert Roles
INSERT INTO roles (name) VALUES ('USER'), ('ADMIN');

-- Assign User Roles (Ensure IDs match users table)
INSERT INTO user_roles (user_id, role_id) 
VALUES 
(1, 1),  -- User ID 1 is a USER
(2, 1),  -- User ID 2 is a USER
(2, 2);  -- User ID 2 is also an ADMIN

-- Insert Exchange Rates (Insert before wallets to maintain integrity)
INSERT INTO currencies (currency_code, exchange_rate) 
VALUES 
('USD', 1.000000), 
('EUR', 0.920000), 
('NGN', 1400.000000);

-- Insert Wallets (After inserting currencies)
INSERT INTO wallets (user_id, currency, balance) 
VALUES 
(1, 'USD', 10000.00),
(1, 'EUR', 5000.00),
(2, 'USD', 50000.00),
(2, 'NGN', 100000.00);

-- Insert Transactions
INSERT INTO transactions (wallet_id, type, amount, currency) 
VALUES 
(1, 'DEPOSIT', 500.00, 'USD'),  
(2, 'DEPOSIT', 200.00, 'EUR');

-- Insert Transfer Transactions (Ensuring recipient wallet exists)
INSERT INTO transactions (wallet_id, type, amount, currency, recipient_wallet_id) 
VALUES 
(1, 'TRANSFER', 100.00, 'USD', 3),  
(3, 'TRANSFER', 50.00, 'USD', 1);

-- Insert Withdrawal Transactions
INSERT INTO transactions (wallet_id, type, amount, currency) 
VALUES 
(1, 'WITHDRAWAL', 200.00, 'USD'), 
(2, 'WITHDRAWAL', 100.00, 'EUR');

-- Verify Data
SELECT * FROM users;
SELECT * FROM roles;
SELECT * FROM user_roles;
SELECT * FROM currencies;
SELECT * FROM wallets;
SELECT * FROM transactions;
