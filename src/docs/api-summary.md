# LilMarket API Summary

This document provides an overview of the LilMarket REST API, including available endpoints, request/response schemas, authentication, and error handling. It is intended to serve as a reference for front-end developers consuming the API.

---

## Authentication

All endpoints (except `/login` and the Swagger UI endpoints) require HTTP Basic authentication.

The username and password are validated against the `users` table.

The `UserRole` determines the allowed actions:

| Role | Permissions |
|------|-------------|
| `ROLE_ADMIN` | Full access (CRUD on all resources) |
| `ROLE_MANAGER` | CRUD on products, batches, sales, customers; read access to users |
| `ROLE_USER` | Read access to products, batches, sales, customers; create sales |

Rate limiting is applied per user role and per IP address using Bucket4j.

The following limits apply:

| Role | Requests per minute |
|------|---------------------|
| `ROLE_USER` | 10 |
| `ROLE_MANAGER` | 100 |
| `ROLE_ADMIN` | 1000 |
| IP | 100 |

---

## General Error Response

All error responses follow the same structure:

```json
{
  "timestamp": "2026-01-30T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation Failed",
  "path": "/api/product",
  "fieldErrors": [
    {
      "field": "name",
      "message": "Product name must be between 2 and 100 characters."
    }
  ]
}
```

- `timestamp`: ISO-8601 UTC timestamp of the error.
- `status`: HTTP status code.
- `error`: Short description of the error.
- `message`: Detailed message.
- `path`: Request URI.
- `fieldErrors`: Optional list of field-level validation errors.

---

## Endpoints

### 1. Authentication

| Method | Path | Description | Request Body | Response | Status Codes |
|--------|------|-------------|--------------|----------|--------------|
| `POST` | `/login` | Authenticates a user and returns user details. | `LoginRequestDTO` | `LoginResponseDTO` | 200, 400, 401 |

**Request Body**

```json
{
  "username": "jdoe",
  "password": "senha123"
}
```

**Response Body**

```json
{
  "id": 1,
  "username": "jdoe",
  "userRole": "ADMIN"
}
```

---

### 2. Users

| Method | Path | Description | Request Body | Response | Status Codes |
|--------|------|-------------|--------------|----------|--------------|
| `POST` | `/user` | Create a new user. | `UserRequestDTO` | `UserResponseDTO` | 201, 400, 409 |
| `GET` | `/user/username/{username}` | Retrieve a user by username. | N/A | `UserResponseDTO` | 200, 404 |
| `GET` | `/user` | List all users. | N/A | `List<UserResponseDTO>` | 200 |
| `PUT` | `/user` | Update an existing user. | `UserRequestDTO` | `UserResponseDTO` | 200, 400, 404 |
| `DELETE` | `/admin/user/{userId}` | Delete a user. | N/A | N/A | 204, 404 |

**UserRequestDTO**

```json
{
  "username": "jdoe",
  "password": "senha123",
  "firstName": "João",
  "userRole": "ADMIN",
  "active": true
}
```

**UserResponseDTO**

```json
{
  "id": 1,
  "username": "jdoe",
  "firstName": "João",
  "userRole": "ADMIN",
  "active": true,
  "createdAt": "2026-01-30T12:00:00Z",
  "lastLogin": "2026-01-30T12:00:00Z"
}
```

---

### 3. Products

| Method | Path | Description | Request Body | Response | Status Codes |
|--------|------|-------------|--------------|----------|--------------|
| `POST` | `/product` | Create a new product. | `ProductRequestDTO` | `ProductResponseDTO` | 201, 400, 409 |
| `GET` | `/product/id/{productId}` | Retrieve a product by ID. | N/A | `ProductResponseDTO` | 200, 404 |
| `GET` | `/product/barcode/{productBarcode}` | Retrieve a product by barcode. | N/A | `ProductResponseDTO` | 200, 404 |
| `GET` | `/product` | List all products. | N/A | `List<ProductResponseDTO>` | 200 |
| `PUT` | `/product` | Update an existing product. | `ProductRequestDTO` | `ProductResponseDTO` | 200, 400, 404 |
| `DELETE` | `/product/{productId}` | Delete a product. | N/A | N/A | 204, 404 |

**ProductRequestDTO**

```json
{
  "name": "Leite integral",
  "barcode": "1234567890123",
  "description": "Leite integral 1L",
  "price": 3.49,
  "categoryId": 2,
  "isPerishable": true
}
```

**ProductResponseDTO**

```json
{
  "id": 10,
  "name": "Leite integral",
  "price": 3.49,
  "categoryName": "Laticínios"
}
```

---

### 4. Product Categories

| Method | Path | Description | Request Body | Response | Status Codes |
|--------|------|-------------|--------------|----------|--------------|
| `POST` | `/category` | Create a new category. | `ProductCategoryRequestDTO` | `ProductCategoryResponseDTO` | 201, 400, 409 |
| `GET` | `/category/id/{categoryId}` | Retrieve a category by ID. | N/A | `ProductCategoryResponseDTO` | 200, 404 |
| `GET` | `/category` | List all categories. | N/A | `List<ProductCategoryResponseDTO>` | 200 |
| `PUT` | `/category` | Update an existing category. | `ProductCategoryRequestDTO` | `ProductCategoryResponseDTO` | 200, 400, 404 |
| `DELETE` | `/category/{categoryId}` | Delete a category. | N/A | N/A | 204, 404 |

**ProductCategoryRequestDTO**

```json
{
  "name": "Laticínios",
  "description": "Leites e derivados"
}
```

**ProductCategoryResponseDTO**

```json
{
  "id": 3,
  "name": "Laticínios",
  "description": "Leites e derivados",
  "createdAt": "2026-01-30T12:00:00Z",
  "updatedAt": "2026-01-30T12:00:00Z"
}
```

---

### 5. Batches

| Method | Path | Description | Request Body | Response | Status Codes |
|--------|------|-------------|--------------|----------|--------------|
| `POST` | `/batch` | Create a new batch. | `BatchRequestDTO` | `BatchResponseDTO` | 201, 400, 409 |
| `GET` | `/batch` | List all batches in stock (paginated). | `Pageable` | `Page<BatchResponseDTO>` | 200 |
| `GET` | `/batch/{batchId}` | Retrieve a batch by ID. | N/A | `BatchResponseDTO` | 200, 404 |
| `GET` | `/batch/in-stock` | Get batches in stock for a product and quantity. | `productId`, `quantity` | `List<BatchResponseDTO>` | 200, 404 |
| `GET` | `/batch/expire-in` | Get batches expiring in X days. | `days` | `List<BatchResponseDTO>` | 200 |
| `POST` | `/batch/report-loss` | Report loss for a batch. | `BatchLossReportRequestDTO` | N/A | 204, 400, 404 |
| `POST` | `/batch/invalidate-stock` | Invalidate a batch. | `batchId`, `BatchInvalidationRequestDTO` | N/A | 204, 400, 404 |

**BatchRequestDTO**

```json
{
  "productId": 10,
  "batchCode": "LOTE-202601",
  "manufactureDate": "2026-01-01",
  "expirationDate": "2026-06-01",
  "quantityInStock": 100,
  "quantityLost": 0,
  "purchasePrice": 1.50
}
```

**BatchResponseDTO**

```json
{
  "batchId": 7,
  "productId": 10,
  "productName": "Coca-Cola 2L",
  "batchCode": "LOTE-202601",
  "manufactureDate": "2026-01-01",
  "expirationDate": "2026-06-01",
  "quantityInStock": 100,
  "quantityLost": 0,
  "purchasePrice": 1.50,
  "createdAt": "2026-01-30T12:00:00Z"
}
```

---

### 6. Sales

| Method | Path | Description | Request Body | Response | Status Codes |
|--------|------|-------------|--------------|----------|--------------|
| `POST` | `/sale` | Create a new sale. | `SaleRequestDTO` | `SaleResponseDTO` | 201, 400, 404, 422 |
| `GET` | `/sale/{saleId}` | Retrieve a sale by ID. | N/A | `SaleResponseDTO` | 200, 404 |
| `GET` | `/sale/by-date` | Get sales between two dates. | `start`, `end` | `List<SaleResponseDTO>` | 200, 400, 404 |
| `PUT` | `/sale` | Update an existing sale. | `SaleRequestDTO` | `SaleResponseDTO` | 200, 400, 404 |

**SaleRequestDTO**

```json
{
  "customerId": 5,
  "userId": 2,
  "items": [
    { "productId": 10, "quantity": 2 },
    { "productId": 12, "quantity": 1 }
  ],
  "amountPaid": 20.00,
  "isOnAccount": false,
  "notes": "Embalar para presente",
  "paymentStatus": "PAID",
  "paymentMethod": "CASH"
}
```

**SaleResponseDTO**

```json
{
  "id": 42,
  "timestamp": "2026-01-30T12:00:00Z",
  "customerName": "Alice Silva",
  "sellerName": "João Souza",
  "items": [
    {
      "productId": 10,
      "productName": "Leite integral",
      "quantity": 2,
      "unitPrice": 3.49,
      "subtotal": 6.98,
      "batchId": 7
    }
  ],
  "totalAmount": 25.00,
  "amountPaid": 25.00,
  "change": 0.00,
  "paymentStatus": "PAID",
  "notes": "Não solicitou cupom fiscal"
}
```

---

## Pagination

The `/batch` endpoint supports Spring Data pagination.

Query parameters:

- `page` (default `0`)
- `size` (default `20`)
- `sort` (e.g., `sort=expirationDate,asc`)

The response includes standard pagination metadata (`totalElements`, `totalPages`, `size`, `number`, etc.).

---

## Logging

All requests and responses are logged by `LoggingFilter` and `LoggingPreAuthFilter`.

Logs include:

- User (or `anonymous`)
- Remote IP
- HTTP method and path
- Status code
- Duration

---

## Rate Limiting

If a request exceeds the allowed rate, the API returns:

```
HTTP/1.1 429 Too Many Requests
```

The response body is the standard error format.

---

## Notes

- All dates are in ISO-8601 UTC format.
- The API uses Spring Boot 3.x, Spring Data JPA, MapStruct, and Bucket4j for rate limiting.
- Swagger UI is available at `/swagger-ui.html` and `/swagger-ui/`.
- The application uses basic authentication; no JWT or OAuth is implemented.