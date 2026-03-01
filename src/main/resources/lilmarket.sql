DROP SCHEMA IF EXISTS `lilmarket`;
CREATE SCHEMA `lilmarket`;
USE `lilmarket`;

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- FORNECEDORES
-- ============================================

CREATE TABLE suppliers (
    supplier_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(100),
    address VARCHAR(100),
    district VARCHAR(100),
    city VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- REABASTECIMENTO DE ESTOQUE
-- ============================================

CREATE TABLE restock (
    restock_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    supplier_id BIGINT NOT NULL,
    restock_invoice TEXT NOT NULL,
    amount_paid DECIMAL(10, 2) NOT NULL,
    bought_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- CATEGORIAS
-- ============================================
CREATE TABLE categories (
    category_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================a
-- PRODUTOS
-- ============================================
CREATE TABLE products (
    product_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    barcode VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    total_quantity DECIMAL(10, 3) DEFAULT 0.000,
    auto_pricing BOOLEAN DEFAULT FALSE,
    profit_margin DECIMAL(10, 2) NOT NULL,
    min_quantity_in_stock INT NOT NULL DEFAULT 0,
    category_id BIGINT,
    unit_type ENUM('COUNT', 'WEIGHT') DEFAULT 'COUNT',
    is_perishable BOOLEAN DEFAULT FALSE,
    alert BOOLEAN DEFAULT FALSE,
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
    batch_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    supplier_id BIGINT NOT NULL,
    restock_id BIGINT NOT NULL,
    batch_code VARCHAR(50) NOT NULL,
    manufacture_date DATE,
    expiration_date DATE NOT NULL,
    original_quantity DECIMAL(10, 3) NOT NULL DEFAULT 0.000,
    quantity_in_stock DECIMAL(10, 3) NOT NULL DEFAULT 0.000,
    quantity_lost DECIMAL(10, 3) NOT NULL DEFAULT 0.000,
    purchase_price DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id),
    FOREIGN KEY (restock_id) REFERENCES restock(restock_id),
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
    customer_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
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
    user_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
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
    sale_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    customer_id BIGINT,
    user_id BIGINT NOT NULL,
    sale_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL,
    amount_paid DECIMAL(10, 2) DEFAULT 0.00,
    net_profit DECIMAL(10, 2) DEFAULT 0.00,
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
    sale_item_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    sale_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    batch_id BIGINT,
    quantity DECIMAL(10, 3) NOT NULL,
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
    payment_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    amount_paid DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('CASH', 'DEBIT', 'CREDIT_CARD', 'PIX') NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,

    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    INDEX idx_customer_pay (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TOKEN DO JWT (SALVAR TOKEN PARA O REFRESH)
-- ============================================
CREATE TABLE refresh_token(
    token_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    token_value TEXT NOT NULL,
    token_expiration_date TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    token_revoked BOOLEAN DEFAULT FALSE,

    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_token (token_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



SET FOREIGN_KEY_CHECKS = 1;



