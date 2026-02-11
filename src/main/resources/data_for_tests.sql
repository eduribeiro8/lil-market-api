USE `lilmarket`;

-- ============================================
-- DADOS INICIAIS ATUALIZADOS
-- ============================================

-- 1. Categorias
INSERT INTO categories (name, description) VALUES 
('ALIMENTOS', 'Produtos de mercearia em geral'), 
('LATICÍNIOS', 'Leites, queijos e derivados'), 
('LIMPEZA', 'Produtos para higiene da casa');

-- 2. Fornecedores
INSERT INTO suppliers (name, phone_number, city) VALUES 
('Distribuidora Vale', '11999998888', 'São Paulo'),
('Laticínios Pureza', '1144445555', 'Campinas');

-- 3. Reabastecimentos (Simulando uma compra de estoque)
INSERT INTO restock (supplier_id, amount_paid, bought_at) VALUES 
(1, 500.00, '2024-01-10 10:00:00'),
(2, 350.00, '2024-01-12 09:00:00');

-- 4. Produtos
-- Colunas: name, barcode, price, profit_margin, category_id, is_perishable, min_quantity_in_stock
INSERT INTO products (name, barcode, price, profit_margin, category_id, is_perishable, min_quantity_in_stock) VALUES
('Arroz Tipo 1 5kg', '7891234567890', 25.90, 30.00, 1, FALSE, 10),
('Feijão Preto 1kg', '7891234567891', 8.50, 35.00, 1, FALSE, 15),
('Leite Integral 1L', '7891234567892', 5.99, 25.00, 2, TRUE, 20),
('Iogurte Natural 170g', '7891234567893', 3.50, 40.00, 2, TRUE, 12);

-- 5. Lotes (Agora vinculados a restock_id e supplier_id)
-- Colunas: product_id, supplier_id, restock_id, batch_code, expiration_date, quantity, quantity_in_stock, purchase_price
INSERT INTO batches (product_id, supplier_id, restock_id, batch_code, manufacture_date, expiration_date, quantity, quantity_in_stock, purchase_price) VALUES
(3, 2, 2, 'L-2024-ABC', '2024-01-10', '2024-03-10', 50, 50, 4.50), -- Leite
(4, 2, 2, 'I-2024-XYZ', '2024-01-12', '2024-02-12', 30, 30, 2.10); -- Iogurte

-- 6. Clientes e Usuário
INSERT INTO customers (first_name, last_name, credit) VALUES 
('João', 'Silva', 0.00),
('Maria', 'Oliveira', 50.00);

-- O admin que já havíamos definido (certifique-se de não duplicar se já rodou)
INSERT IGNORE INTO users (user_name, password, role, active) 
VALUES ('admin', '$2a$12$osiy5mvdV7RfOqs1wPgwbeYBrkCJ4C6XeL0/kZKwdsmBPmQVBYcOm', 'ROLE_ADMIN', TRUE);
