# API Documentation

## Notification Service

Base URL:

```
/api/notifications
```

The Notification Service is responsible for sending email notifications related to orders and payments.


---

## Send order confirmation email

### POST

```
/api/notifications/order
```

### Description

Sends an email notification with information about created order.


### Request Body

```json
{
  "notification": {
    "email": "customer@example.com",
    "message": "Order confirmation"
  },
  "orderId": 123,
  "status": "CREATED",
  "price": 199.99,
  "shippingAddress": {
    "street": "Main Street 1",
    "city": "Krakow",
    "zipCode": "30-001"
  }
}
```


### Response

```text
"Email sent successfully"
```


### Status Codes

| Code | Description |
|------|-------------|
| 200 | Email sent successfully |
| 400 | Invalid request data |
| 500 | Email sending error |


---

## Send payment confirmation email

### POST

```
/api/notifications/payment
```

### Description

Sends an email notification about payment status.


### Request Body

```json
{
  "notification": {
    "email": "customer@example.com",
    "message": "Payment confirmation"
  },
  "status": "SUCCESS",
  "amount": 199.99,
  "paymentDate": "2026-06-25T12:00:00",
  "orderId": 123
}
```


### Response

```text
"Email sent successfully"
```


### Status Codes

| Code | Description |
|------|-------------|
| 200 | Email sent successfully |
| 400 | Invalid request data |
| 500 | Email sending error |


# Order Service

Base URL:

```
/api/orders
```

The Order Service is responsible for creating, updating and managing customer orders.


---

# Get all orders

## GET

```
/api/orders
```

### Description

Returns a list of all orders.

### Response

```json
[
  {
    "id": 1,
    "status": "CREATED",
    "items": [],
    "price": 199.99,
    "shippingAddress": {
      "street": "Main Street 1",
      "city": "Krakow"
    },
    "userEmail": "user@example.com"
  }
]
```

### Status Codes

| Code | Description |
|------|-------------|
| 200 | Orders returned successfully |


---

# Get order by id

## GET

```
/api/orders/{orderId}
```

### Description

Returns order by identifier.

### Example

```
GET /api/orders/1
```


### Response

```json
{
  "id": 1,
  "status": "CREATED",
  "items": [],
  "price": 199.99,
  "userEmail": "user@example.com"
}
```


---

# Create order

## POST

```
/api/orders
```

### Description

Creates a new order.


### Request Body

```json
{
  "userEmail": "user@example.com",
  "shippingAddress": {
    "street": "Main Street 1",
    "city": "Krakow"
  }
}
```


### Response

```json
{
  "id": 1,
  "status": "CREATED",
  "price": 0
}
```


---

# Update order

## PUT

```
/api/orders
```

### Description

Updates existing order.


### Request Body

```json
{
  "id": 1,
  "status": "PAID",
  "price": 299.99
}
```


### Response

Returns updated order object.


---

# Delete order

## DELETE

```
/api/orders
```

### Description

Deletes order by id.


### Request Body

```json
{
  "id": 1
}
```


### Response

```text
Order deleted successfully
```


---

# Add product to order

## POST

```
/api/orders/{orderId}
```

### Description

Adds product with selected quantity to existing order.


### Parameters

| Parameter | Type | Description |
|-|-|-|
| `orderId` | int | Order identifier |
| `productId` | String | Product identifier |
| `quantity` | int | Amount of product |


### Example

```
POST /api/orders/1?productId=abc123&quantity=2
```


### Response

```json
{
  "id": 10,
  "productId": "abc123",
  "quantity": 2
}
```


---

# Get user orders

## POST

```
/api/orders/userOrders
```

### Description

Returns orders based on provided order IDs.


### Request Body

```json
[
  1,
  2,
  3
]
```


### Response

```json
[
  {
    "id":1,
    "status":"CREATED"
  }
]
```


---

# Get order items

## GET

```
/api/orders/orderItems/{orderId}
```


### Description

Returns all items belonging to specific order.


### Example

```
GET /api/orders/orderItems/1
```


### Response

```json
[
  {
    "productId":"abc123",
    "quantity":2
  }
]
```

# Payment Service

Base URL:

```
/api/payments
```

The Payment Service is responsible for creating payments, managing payment status and integrating with Stripe payment provider.


---

# Create payment

## POST

```
/api/payments
```

### Description

Creates a new payment record.


### Request Body

```json
{
  "orderId": 1,
  "amount": 199.99,
  "status": "PENDING"
}
```


### Response

```json
{
  "id": 1,
  "status": "PENDING",
  "amount": 199.99,
  "paymentDate": "2026-06-25T12:00:00",
  "orderId": 1,
  "emailSendTo": "user@example.com"
}
```


---

# Get payment by order id

## GET

```
/api/payments/{orderId}
```

### Description

Returns payments assigned to specific order.


### Example

```
GET /api/payments/1
```


### Response

```json
[
  {
    "id":1,
    "status":"SUCCESS",
    "amount":199.99,
    "orderId":1
  }
]
```


---

# Add order to payment

## POST

```
/api/payments/{orderId}
```

### Description

Assigns an order to an existing payment.


### Parameters

| Parameter | Type | Description |
|-|-|-|
| `orderId` | Integer | Order identifier |
| `paymentId` | Integer | Payment identifier |


### Example

```
POST /api/payments/1?paymentId=5
```


### Response

```json
{
  "id":5,
  "orderId":1,
  "status":"PENDING"
}
```


---

# Get payment status

## GET

```
/api/payments/status/{orderId}
```

### Description

Returns current payment status for order.


### Example

```
GET /api/payments/status/1
```


### Response

```json
"SUCCESS"
```


---

# Create Stripe payment

## POST

```
/api/payments/create
```

### Description

Creates Stripe payment session.


### Request Body

```json
{
  "amount":199.99,
  "currency":"PLN"
}
```


### Response

```json
{
  "id":"payment_id",
  "status":"created"
}
```


---

# Create hosted checkout

## POST

```
/api/payments/hostedCheckout
```

### Description

Creates Stripe hosted checkout session.


### Request Body

```json
{
  "orderId":1,
  "price":199.99
}
```


### Response

```json
{
  "url":"https://checkout.stripe.com/..."
}
```


---

# Stripe Webhook

## POST

```
/webhooks/stripe
```

### Description

Handles Stripe webhook events and updates payment status.


### Headers

| Header | Description |
|-|-|
| `Stripe-Signature` | Stripe webhook signature used for verification |


### Request Body

```text
Stripe event payload
```


### Response

```text
Webhook processed successfully
```

# Product Service

Base URL:

```
/api/products
```

The Product Service is responsible for managing products, inventory and providing product data for orders.


---

# Get all products

## GET

```
/api/products
```

### Description

Returns list of all available products.

### Response

```json
[
  {
    "id": "64f1a2b3c9",
    "name": "iPhone",
    "description": "Smartphone",
    "price": 1000,
    "category": "Electronics",
    "amount": 10
  }
]
```


---

# Get product by id

## GET

```
/api/products/{productId}
```

### Description

Returns product by its ID.

### Example

```
GET /api/products/64f1a2b3c9
```

### Response

```json
{
  "id": "64f1a2b3c9",
  "name": "iPhone",
  "description": "Smartphone",
  "price": 1000,
  "category": "Electronics",
  "amount": 10
}
```


---

# Create product

## POST

```
/api/products
```

### Description

Creates a new product.

### Request Body

```json
{
  "name": "iPhone",
  "description": "Smartphone",
  "price": 1000,
  "category": "Electronics",
  "amount": 10
}
```

### Response

```json
{
  "id": "generated-id",
  "name": "iPhone",
  "description": "Smartphone",
  "price": 1000,
  "category": "Electronics",
  "amount": 10
}
```


---

# Update product

## PUT

```
/api/products
```

### Description

Updates existing product.

### Request Body

```json
{
  "id": "64f1a2b3c9",
  "name": "iPhone 15",
  "description": "Updated model",
  "price": 1200,
  "category": "Electronics",
  "amount": 8
}
```

### Response

```json
{
  "id": "64f1a2b3c9",
  "name": "iPhone 15",
  "description": "Updated model",
  "price": 1200,
  "category": "Electronics",
  "amount": 8
}
```


---

# Delete product

## DELETE

```
/api/products/{productId}
```

### Description

Deletes product by id.

### Example

```
DELETE /api/products/64f1a2b3c9
```

### Response

```text
product deleted successfully
```


---

# Add product to order

## POST

```
/api/products/addToOrder
```

### Description

Adds product to order with given quantity and returns created OrderItem.


### Query Parameters

| Parameter | Type | Description |
|----------|------|-------------|
| productId | String | Product identifier |
| quantity | int | Number of items |


### Example

```
POST /api/products/addToOrder?productId=64f1a2b3c9&quantity=2
```


### Response

```json
{
  "id": 1,
  "productId": "64f1a2b3c9",
  "name": "iPhone",
  "description": "Smartphone",
  "price": 1000,
  "quantity": 2
}
```

# Auth Service

Base URL:

```
/api/auth
```

The Auth Service is responsible for user authentication, registration, JWT token generation and refresh token handling.


---

## Register user

### POST

```
/api/auth/register
```

### Description

Registers a new user in the system.

### Request Body

```json
{
  "username": "john",
  "password": "123456",
  "email": "john@example.com"
}
```

### Response

```json
{
  "accessToken": "jwt_token",
  "refreshToken": "refresh_token",
  "userId": 1,
  "role": "USER"
}
```


---

## Login

### POST

```
/api/auth/login
```

### Description

Authenticates user and returns JWT tokens.

### Request Body

```json
{
  "email": "john@example.com",
  "password": "123456"
}
```

### Response

```json
{
  "accessToken": "jwt_token",
  "refreshToken": "refresh_token",
  "userId": 1,
  "role": "USER"
}
```


---

## Refresh token

### POST

```
/api/auth/refresh
```

### Description

Generates new access token using refresh token.

### Request Body

```json
{
  "refreshToken": "refresh_token_here"
}
```

### Response

```json
{
  "accessToken": "new_jwt_token"
}
```


---

## Create admin

### POST

```
/api/auth/create-admin
```

### Description

Creates a new admin user.

### Request Body

```json
{
  "username": "admin",
  "password": "123456",
  "email": "admin@example.com"
}
```

### Response

```json
{
  "accessToken": "jwt_token",
  "refreshToken": "refresh_token",
  "userId": 1,
  "role": "ADMIN"
}
```


---

# User Service

Base URL:

```
/api/users
```

The User Service is responsible for managing user data and linking orders to users.


---

## Get all users

### GET

```
/api/users
```

### Response

```json
[
  {
    "id": 1,
    "username": "john",
    "email": "john@example.com",
    "role": "USER",
    "orderIds": [1, 2, 3]
  }
]
```


---

## Get user by id

### GET

```
/api/users/{userId}
```

### Example

```
GET /api/users/1
```


---

## Get user orders

### POST

```
/api/users/orders/{userId}
```

### Description

Returns all orders assigned to a user.

### Example

```
POST /api/users/orders/1
```


### Response

```json
[
  {
    "orderId": 1,
    "status": "CREATED",
    "price": 199.99
  }
]
```


---

## Add order to user

### POST

```
/api/users/addOrder/{userId}/{orderId}
```

### Description

Assigns an order to a user.

### Example

```
POST /api/users/addOrder/1/10
```

### Response

```json
{
  "id": 1,
  "username": "john",
  "orderIds": [10, 11]
}
```