# EShop - E-commerce Microservices

An online store system that allows users to browse products, create order and make payments. 

The project was implemented as a microservice application.

## ✅Main function:
- product management
- ordering
- payment processing
- sending email notification
- product caching

## 🛠 Tech Stack

**Backend:**
- Java 21 + Spring Boot 4+
- Spring Cloud(Eureka, API Gateway)
- Spring Security + JWT Token
- Stripe Payment
- Spring Mail

**Database:**
- PostgreSQL
- MongoDB
- Redis

**Communication:**
- REST API
- Apache Kafka

**DevOps:**
- Docker 
- Docker Compose

## 🏗 Architecture

```
Client (React)
      ↓
API Gateway (port 8765) — JWT filter
      ↓
┌─────────────────────────────────────┐
│           Eureka Service Registry    │
└─────────────────────────────────────┘
      ↓
┌──────────────┬───────────────┬──────────────┬───────────────┐
│ user-service │product-service│ order-service│payment-service│
│   :8081      │    :8082      │    :8083     │    :8084      │
│ PostgreSQL   │   MongoDB     │ PostgreSQL   │ PostgreSQL    │
└──────────────┴───────────────┴──────────────┴───────────────┘
                          ↓
                    Apache Kafka
                          ↓
               notification-service :8085
```

## 🚀 Local Launch


### Requirements
- Java 21
- Maven 
- Docker + Docker Compose

### Steps

1. Clone repository 

```bash
git clone https://github.com/username/task-management-microservices
cd task-management-microservices
```

2. Build any Service

```bash
mvn clean package -DskipTests
```

3. Run with Docker Compose

```bash
docker-compose up --build
```

4. Application available at:
- Frontend: http://localhost:3000
- API Gateway: http://localhost:8765
- Eureka: http://localhost:8761
- Kafdrop: http://localhost:9000

## 🔐 Environment variables

## Environment Variables

| Variable                | Description                  |
|------------------------|------------------------------|
| `JWT_SECRET`          | JWT signing key             |
| `MAIL_PASSWORD`       | Email account password used for sending emails |
| `MAIL_USERNAME`       | Email address used for sending emails |
| `PRODUCT_MONGO_URI`   | URI to MongoDB database for product service |
| `EUREKA_URI`          | Eureka Server address |
| `STRIPE_SECRET_KEY`   | Stripe API secret key used for payments |
| `STRIPE_WEBHOOK_SECRET` | Stripe webhook verification secret |

## 📡 API Endpoints

### User Service
| Metoda | Endpoint | Opis             |
|--------|----------|------------------|
| POST | `/api/auth/register` | Registration     |
| POST | `/api/auth/login` | Loggin           |
| POST | `/api/auth/refresh` | Refreshing token |

### Project Service
| Metoda | Endpoint                    | Opis               |
|--------|-----------------------------|--------------------|
| POST | `/api/products`             | Creating a product |
| GET | `/api/products/{productId}` | Get a product      |
| PUT | `/api/products`             | Update a product   |
| DELETE | `/api/products/{productId}` | Delete a product   |

### Order Service
| Metoda | Endpoint             | Opis              |
|--------|----------------------|-------------------|
| POST | `/api/orders`        | Creating an order |
| GET | `/api/orders/{orderId}` | Get an order      |
| PUT | `/api/orders`        | Update an order   |
| DELETE | `/api/orders` | Delete an order   |