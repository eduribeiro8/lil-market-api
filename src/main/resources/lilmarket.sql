DROP SCHEMA IF EXISTS `lilmarket`;

CREATE SCHEMA `lilmarket`;

use `lilmarket`;

SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE products (
    product_id INT PRIMARY KEY NOT NULL auto_increment,
    name VARCHAR(255) NOT NULL,
    barcode long NOT NULL,
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
    debt double
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


CREATE TABLE sales (
    sale_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    customer_id INT,
    sale_date DATETIME,
    total_amount DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers (customer_id)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4;


CREATE TABLE sale_items (
    sale_id INT ,
    product_id INT ,
    quantity INT,
    price DOUBLE,
    FOREIGN KEY (sale_id) REFERENCES sales (sale_id),
    FOREIGN KEY (product_id) REFERENCES products (product_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
