CREATE DATABASE IF NOT EXISTS goldspice_store;
USE goldspice_store;

CREATE TABLE IF NOT EXISTS user (
    User_id   INT AUTO_INCREMENT PRIMARY KEY,
    Name      VARCHAR(100) NOT NULL,
    Username  VARCHAR(50)  NOT NULL UNIQUE,
    Password  VARCHAR(255) NOT NULL, -- PBKDF2 hash, not plain text
    User_role VARCHAR(20)  NOT NULL,
    Join_date DATE
);

CREATE TABLE IF NOT EXISTS products (
    P_id       INT PRIMARY KEY,
    P_name     VARCHAR(100) NOT NULL,
    P_price    DOUBLE NOT NULL,
    StockLevel INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS bills (
    bill_id       INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100),
    bill_date     DATE NOT NULL,
    total_amount  DOUBLE NOT NULL,
    cashier_id    INT NOT NULL
);

CREATE TABLE IF NOT EXISTS bill_items (
    item_id    INT AUTO_INCREMENT PRIMARY KEY,
    bill_id    INT NOT NULL,
    P_id       INT NOT NULL,
    quantity   INT NOT NULL,
    unit_price DOUBLE NOT NULL,
    subtotal   DOUBLE NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES bills(bill_id) ON DELETE CASCADE
);

INSERT IGNORE INTO user (Name, Username, Password, User_role, Join_date) VALUES
 ('Store Admin',   'admin1',   '100000:XbJpp5I3bSD9VHXo5E5A8w==:s1ZmMB3zZLiXGdz83/PvxQMEDrlFFiIegpOwdPOSWMY=',   'Admin',   CURDATE()),
 ('Store Manager', 'manager1', '100000:xfCeg9CguQ8YeFvpw80Wfg==:u7Xq+AJC/BE7U1PjJc/e0fkmk0+R6jBBHwm9dffxadM=', 'Manager', CURDATE()),
 ('Store Cashier', 'cashier1', '100000:DfEu4BHB7U6OlM2499K1KQ==:7OcFaOvq07Ruy5VUxCOERwwy92BwbeOwb96he0EKGkg=', 'Cashier', CURDATE());

INSERT IGNORE INTO products (P_id, P_name, P_price, StockLevel) VALUES
 (1, 'Turmeric Powder', 450.00, 100),
 (2, 'Black Pepper',    900.00, 80),
 (3, 'Cardamom',       2400.00, 40);
