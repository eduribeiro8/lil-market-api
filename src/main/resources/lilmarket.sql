DROP SCHEMA IF EXISTS `lilmarket`;
CREATE SCHEMA `lilmarket`;
USE `lilmarket`;

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- CATEGORIAS
-- ============================================
CREATE TABLE categories (
    category_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- PRODUTOS
-- ============================================
CREATE TABLE products (
    product_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    barcode VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category_id INT,
    is_perishable BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL,
    INDEX idx_barcode (barcode),
    INDEX idx_category (category_id),
    INDEX idx_perishable (is_perishable),

    CONSTRAINT chk_price_positive CHECK (price >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- LOTES (apenas para produtos perecíveis)
-- ============================================
CREATE TABLE batches (
    batch_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    product_id INT NOT NULL,
    batch_code VARCHAR(50) NOT NULL,
    manufacture_date DATE,
    expiration_date DATE NOT NULL,
    quantity_in_stock INT NOT NULL DEFAULT 0,
    quantity_lost INT NOT NULL DEFAULT 0,
    purchase_price DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    UNIQUE KEY unique_product_batch (product_id, batch_code),

    INDEX idx_product_expiration (product_id, expiration_date),
    INDEX idx_expiration (expiration_date),

    CONSTRAINT chk_batch_quantity CHECK (quantity_in_stock >= 0),
    CONSTRAINT chk_dates CHECK (expiration_date >= manufacture_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- CLIENTES
-- ============================================
CREATE TABLE customers (
    customer_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(20),
    address TEXT,
    credit DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_email (email),
    INDEX idx_phone (phone_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- USUÁRIOS
-- ============================================
CREATE TABLE users (
    user_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_name VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    role ENUM('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER') DEFAULT 'ROLE_USER',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,

    INDEX idx_username (user_name),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- VENDAS
-- ============================================
CREATE TABLE sales (
    sale_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    customer_id INT,
    user_id INT NOT NULL,
    sale_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL,
    amount_paid DECIMAL(10, 2) DEFAULT 0.00,
    payment_status ENUM('PENDING', 'PAID', 'PARTIAL', 'CANCELLED') DEFAULT 'PENDING',
    notes TEXT,

    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),

    INDEX idx_timestamp (sale_timestamp),
    INDEX idx_customer (customer_id),
    INDEX idx_user (user_id),
    INDEX idx_status (payment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- ITENS DE VENDA
-- ============================================
CREATE TABLE sale_items (
    sale_item_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    sale_id INT NOT NULL,
    product_id INT NOT NULL,
    batch_id INT,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,

    FOREIGN KEY (sale_id) REFERENCES sales(sale_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (batch_id) REFERENCES batches(batch_id),

    INDEX idx_sale (sale_id),
    INDEX idx_product (product_id),

    CONSTRAINT chk_quantity CHECK (quantity > 0),
    CONSTRAINT chk_prices CHECK (unit_price >= 0 AND subtotal >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- PAGAMENTOS (Histórico de acertos de contas)
-- ============================================
CREATE TABLE customer_payments (
    payment_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    customer_id INT NOT NULL,
    amount_paid DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('CASH', 'DEBIT', 'CREDIT_CARD', 'PIX') NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,

    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    INDEX idx_customer_pay (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- DADOS INICIAIS ATUALIZADOS
-- ============================================

INSERT INTO categories (name) VALUES ('ALIMENTOS'), ('LATICÍNIOS'), ('BEBIDAS'), ('LIMPEZA');

INSERT INTO products (name, barcode, description, price, category_id, is_perishable) VALUES
('Arroz Tipo 1 5kg', '7891234567890', 'Arroz branco tipo 1', 25.90, 1, FALSE),
('Feijão Preto 1kg', '7891234567891', 'Feijão preto', 8.50, 1, FALSE),
('Leite Integral 1L', '7891234567892', 'Leite integral pasteurizado', 5.99, 2, TRUE),
('Iogurte Natural 170g', '7891234567893', 'Iogurte natural', 3.50, 2, TRUE);

-- Lotes para produtos perecíveis
INSERT INTO batches (product_id, batch_code, manufacture_date, expiration_date, quantity_in_stock, purchase_price) VALUES
(3, 'LEITE-2024-001', '2024-01-10', '2024-02-10', 48, 4.50),
(3, 'LEITE-2024-002', '2024-01-15', '2024-02-15', 36, 4.50),
(4, 'IOG-2024-001', '2024-01-12', '2024-02-05', 60, 2.80);

-- Cliente de exemplo
INSERT INTO customers (first_name, last_name, email, phone_number, credit) VALUES
('João', 'Santos', 'joao.santos@email.com', '11987654321', 0.00),
('Ana', 'Costa', 'ana.costa@email.com', '11912345678', 25.00);

INSERT INTO lilmarket.users
(user_id, user_name, password, first_name, `role`, active, created_at, last_login)
VALUES(0, 'admin', '$2a$12$osiy5mvdV7RfOqs1wPgwbeYBrkCJ4C6XeL0/kZKwdsmBPmQVBYcOm', NULL, 'ROLE_ADMIN', 1, current_timestamp(), NULL);
