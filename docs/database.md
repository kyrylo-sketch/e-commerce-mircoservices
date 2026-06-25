# Database Design

The system uses a polyglot persistence approach with multiple databases depending on service responsibility.

- PostgreSQL → Orders, Payments, Users
- MongoDB → Products
- Redis → Cache layer (if enabled)


---

# Product Service (MongoDB)

MongoDB is used to store product catalog data.


## Collection: products

```json
{
  "_id": "64f1a2b3c9",
  "name": "iPhone",
  "description": "Smartphone",
  "price": 1000,
  "category": "Electronics",
  "amount": 10
}
```

### Notes
- Document-based storage
- Flexible schema for product attributes
- Optimized for fast reads


---

# Order Service (PostgreSQL)

Orders are stored in relational database.


## Table: orders

| Column           | Type        | Description              |
|------------------|-------------|--------------------------|
| id               | int         | Primary key              |
| status           | varchar     | Order status             |
| price            | double      | Total order price        |
| user_email       | varchar     | Email of user            |
| shipping_address  | json        | Delivery address         |


---

## Table: order_items

| Column      | Type    | Description            |
|-------------|--------|------------------------|
| id          | int    | Primary key            |
| product_id  | string | Reference to product    |
| name        | string | Product name snapshot   |
| price       | double | Product price snapshot  |
| quantity    | int    | Quantity in order       |


### Relationships

- One Order → Many OrderItems


---

# Payment Service (PostgreSQL)

Payments are stored independently from orders.


## Table: payments

| Column        | Type      | Description                  |
|---------------|-----------|------------------------------|
| id            | int       | Primary key                  |
| status        | varchar   | Payment status               |
| amount        | double    | Payment amount               |
| payment_date  | timestamp | Date of payment              |
| order_id      | int       | Related order ID             |
| email_send_to | varchar   | Email notification address    |


---

# User Service (PostgreSQL)

Users and authentication data.


## Table: users

| Column      | Type        | Description           |
|-------------|-------------|-----------------------|
| id          | int         | Primary key           |
| username    | varchar     | Username              |
| password    | varchar     | Encrypted password    |
| email       | varchar     | User email           |
| role        | varchar     | USER / ADMIN         |


---

## Table: user_order_ids

(Stored as `@ElementCollection`)

| Column    | Type | Description        |
|-----------|------|--------------------|
| user_id   | int  | User reference     |
| order_ids | int  | Order identifiers  |


---

# Cache (Redis)

Redis is optionally used for:

- product caching
- session / token caching (if enabled)
- reducing database load


---

# Data Flow Overview

1. User places order → Order Service
2. Order stored in PostgreSQL
3. Payment created → Payment Service
4. Payment updates order status
5. Kafka event sent
6. Notification Service sends email
7. Product stock updated in MongoDB


---

# Notes

- Services do NOT share databases directly
- Communication is done via REST and Kafka
- Each service owns its own data