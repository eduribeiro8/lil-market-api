DROP SCHEMA IF EXISTS `lilmarket`;

CREATE SCHEMA `lilmarket`;

use `lilmarket`;

SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE products (
    product_id INT PRIMARY KEY NOT NULL auto_increment,
    name VARCHAR(255) NOT NULL,
    barcode VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50),
    quantity_in_stock INT
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


CREATE TABLE customers (
    customer_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(20),
    address TEXT,
    debt DECIMAL(10, 2)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


CREATE TABLE sales (
    sale_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    customer_id INT,
    sale_timestamp TIMESTAMP,
    sale_date DATETIME,
    total_amount DECIMAL(10, 2) NOT NULL,
    amount_paid DECIMAL(10, 2) DEFAULT 0.00, -- Amount paid by the customer
    payment_status ENUM('PAYMENT_PENDING', 'PAYMENT_PAID', 'PAYMENT_CANCELLED', 'PAYMENT_PARTLY_PAID', 'PAYMENT_REFUNDED', 'PAYMENT_DEBT') DEFAULT 'PAYMENT_PENDING',
    FOREIGN KEY (customer_id) REFERENCES customers (customer_id)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4;


CREATE TABLE sale_items (
    sale_id INT ,
    product_id INT ,
    quantity INT,
    price DECIMAL(10, 2),
    FOREIGN KEY (sale_id) REFERENCES sales (sale_id),
    FOREIGN KEY (product_id) REFERENCES products (product_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE users (
	id INT NOT NULL AUTO_INCREMENT,
    user_name VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    role VARCHAR(20) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_active_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- CREATE TABLE roles (
--   user_name VARCHAR(50) NOT NULL,
--   role VARCHAR(50) NOT NULL,
--   UNIQUE KEY authorities5_idx_1 (user_name, role),
--   CONSTRAINT authorities5_ibfk_1 FOREIGN KEY (user_name) REFERENCES users (user_name)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
