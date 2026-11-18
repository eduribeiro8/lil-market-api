---
name: stock-and-financial-invariants
description: >-
  Use this skill when implementing or modifying business logic involving Sales,
  Batches, Restocks, Products, StockMovements, Reports, or Customer credit/debt
  (fiado) in LilMarket API. Preserves numeric scales, FEFO stock consistency,
  profit calculations, and payment invariants.
---

# Stock, Batch & Financial Domain Invariants

Market operations in LilMarket span multiple coupled aggregates. Any change to stock or financial flows must preserve the invariants below.

## 1. Numeric Precision & Scales

Never mix monetary and quantity scales or compare `BigDecimal` values with `.equals(...)` (use `.compareTo(...) == 0`):

- **Monetary & Margin Values (`DECIMAL(10, 2)`):**
  - Fields: `Product.price`, `Product.avgPrice`, `Product.profitMargin`, `Batch.purchasePrice`, `Restock.amountPaid`, `Sale.discount`, `Sale.total`, `Sale.amountPaid`, `Sale.netProfit`, `SaleItem.unitPrice`, `SaleItem.unitDiscount`, `SaleItem.subtotal`, `Customer.credit`, `CustomerPayment.amountPaid`.
  - Always round divisions and price recalculations with scale `2` and `RoundingMode.HALF_UP`.
- **Quantities (`DECIMAL(10, 3)`):**
  - Fields: `Product.totalQuantity`, `Batch.originalQuantity`, `Batch.quantityInStock`, `Batch.quantityLost`, `SaleItem.quantity`, `StockMovement.quantity`.
  - Scale `3` supports both `UnitType.COUNT` (unitary items) and `UnitType.WEIGHT` (fractional kg/g).

## 2. Tri-Layer Stock Consistency

Stock state exists across three places. Whenever stock enters, exits, expires, or is adjusted, verify all three stay aligned within the `@Transactional` boundary:

1. **`Batch` (`BatchServiceImpl`):**
   - Stock consumption follows expiration order (**FEFO** — earliest valid `expirationDate` first).
   - Enforce `quantityInStock >= 0` (throw `InsufficientQuantityInSaleException` -> `422` when total available across valid batches is insufficient) and `expirationDate >= manufactureDate` (`InvalidDateIntervalException` -> `400`).
   - Blank `batchCode` on restock generates an automatic code via `BatchServiceImpl` while preventing duplicate codes within the same restock payload and in the database (`DuplicateBatchCodeException` -> `409`).
   - Invalidations/perishable losses increment `Batch.quantityLost` and decrement `Batch.quantityInStock`.

2. **`Product` (`ProductServiceImpl`):**
   - Keep `Product.totalQuantity` synchronized with batch additions, sales decrements, losses, and manual adjustments.
   - On restock (`RestockServiceImpl`), trigger `productService.calculatePriceBasedOnStock(productId)` so `avgPrice` (weighted by current stock batches) and `price` (when `autoPricing == true` using `profitMargin`) are updated; explicit `BatchRequestDTO.sellingPrice()` overrides `price` afterward.
   - Maintain `Product.alert` relative to `minQuantityInStock`.

3. **`StockMovement` Ledger (`StockMovementServiceImpl`):**
   - Every physical stock change must record a `StockMovement` (`MovementType.ENTRY`, `EXIT`, `LOSS`, or manual adjustment) with UTC `OffsetDateTime` and `referenceId` (`restockId`, `saleId`, or `batchId`).

## 3. Sales, Profit & Customer Account ("Fiado") Invariants

Reference: `SaleServiceImpl` & `CustomerServiceImpl`.

- **Batch Splitting per SaleItem:** A single `SaleItemRequestDTO` quantity may span multiple `Batch` rows. Create one persisted `SaleItem` per consumed `Batch` so per-batch `purchasePrice` and traceability (`batch_id`) remain exact.
- **Net Profit Formula:**
  - Per split item: `(product.price - batch.purchasePrice - item.unitDiscount) * quantityFromThisBatch`.
  - Subtract global `saleRequestDTO.discount()` once from both `Sale.total` and `Sale.netProfit`.
- **Payment Status & Fiado (`Customer.credit`):**
  - `Sale.resolvePaymentStatus()` determines `PAID`, `PARTIAL`, or `PENDING` comparing `amountPaid` vs `total`.
  - If `paymentStatus != PaymentStatus.PAID`:
    - Require `saleRequestDTO.isOnAccount() == true`; otherwise throw `BusinessException` (`422`).
    - Call `customer.addDebt(total.subtract(amountPaid))` (which subtracts from `Customer.credit`).
  - Whenever `amountPaid > 0`, persist a `CustomerPayment` record linked to the `Customer`.
  - Direct deposits (`/customer/{id}/deposit`, restricted to `MANAGER`/`ADMIN`) call `customer.addCredit(...)` and record a `CustomerPayment`.

## Validation Steps

1. Run `./mvnw test -Dtest=SaleServiceImplTest,BatchServiceImplTest,RestockServiceImplTest,ProductServiceImplTest,StockMovementServiceImplTest,CustomerServiceImplTest`.
2. Check edge cases: multi-batch sale item split, `unitDiscount` + global `discount` impact on `netProfit`, `isOnAccount = false` with partial payment, and `BigDecimal` scale `2` vs `3`.
