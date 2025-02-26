-- Drop existing tables if they exist
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS wallets;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS currencies;
DROP TABLE IF EXISTS users;

-- Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR_IGNORECASE(50) NOT NULL UNIQUE,
    email VARCHAR_IGNORECASE(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Roles Table
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR_IGNORECASE(50) NOT NULL UNIQUE
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
    exchange_rate DECIMAL(10,6) NOT NULL DEFAULT 1.000000,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Wallets Table
CREATE TABLE wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (currency) REFERENCES currencies(currency_code) ON DELETE CASCADE,
    CHECK (balance >= 0)
);

-- Transactions Table
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('DEPOSIT', 'TRANSFER', 'WITHDRAWAL')),
    amount DECIMAL(15,2) NOT NULL CHECK (amount >= 0.01),
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    recipient_wallet_id BIGINT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_wallet_id) REFERENCES wallets(id) ON DELETE SET NULL,
    FOREIGN KEY (currency) REFERENCES currencies(currency_code) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert Users
INSERT INTO users (username, email, password) 
VALUES 
('kamzee1995', 'kamzycoded@gmail.com', 'hashedPassword1'), 
('kamil1995', 'kamzeemazeratti@gmail.com', 'hashedPassword2');

-- Insert Roles
INSERT INTO roles (name) VALUES ('USER'), ('ADMIN');

-- Assign User Roles
INSERT INTO user_roles (user_id, role_id) 
VALUES 
(1, 1),  
(2, 1),  
(2, 2);  

-- Insert Currencies
INSERT INTO currencies (currency_code, exchange_rate) 
VALUES 
('USD', 1.000000), 
('EUR', 0.920000), 
('NGN', 1400.000000);

-- Insert Wallets
INSERT INTO wallets (user_id, currency, balance) 
VALUES 
(1, 'USD', 10000.00),
(1, 'EUR', 5000.00),
(2, 'USD', 50000.00),
(2, 'NGN', 100000.00);

-- Insert Transactions
INSERT INTO transactions (wallet_id, user_id, type, amount, currency) 
VALUES 
(1, 1, 'DEPOSIT', 500.00, 'USD'),  
(2, 1, 'DEPOSIT', 200.00, 'EUR');

-- Insert Transfer Transactions
INSERT INTO transactions (wallet_id, user_id, type, amount, currency, recipient_wallet_id) 
VALUES 
(1, 1, 'TRANSFER', 100.00, 'USD', 3),  
(3, 2, 'TRANSFER', 50.00, 'USD', 1);

-- Insert Withdrawal Transactions
INSERT INTO transactions (wallet_id, user_id, type, amount, currency) 
VALUES 
(1, 1, 'WITHDRAWAL', 200.00, 'USD'), 
(2, 1, 'WITHDRAWAL', 100.00, 'EUR');

-- Verify Data
SELECT * FROM users;
SELECT * FROM roles;
SELECT * FROM user_roles;
SELECT * FROM currencies;
SELECT * FROM wallets;
SELECT * FROM transactions;
