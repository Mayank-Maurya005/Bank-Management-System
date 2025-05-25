CREATE DATABASE IF NOT EXISTS bankdb;
USE bankdb;


CREATE TABLE IF NOT EXISTS accounts (
    account_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    account_type ENUM('Current', 'Savings', 'Student') NOT NULL,
    balance DOUBLE DEFAULT 0,
    institution_name VARCHAR(60),
    max_with_Limit INT,
    tradeLicenseNumber VARCHAR(14)
);
