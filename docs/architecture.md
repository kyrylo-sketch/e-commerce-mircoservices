# Architecture

System is based on microservice architecture.

It consists of independent services communicating via REST and Kafka.

## Services

- User Service - manages users and security
- Product Service - manages products and inventory
- Order Service - handles orders creation and processing
- Payment Service - processes payments
- Notification Service - sends emails


## System diagram

```mermaid
graph TD

EurekaServer --> UserService
EurekaServer --> ProductService
EurekaServer --> OrderService
EurekaServer --> PaymentService

Client --> API_Gateway
API_Gateway --> UserService
API_Gateway --> ProductService
API_Gateway --> OrderService
API_Gateway --> PaymentService

UserService --> PostgreSQL
OrderService --> PostgreSQL
ProductService --> MongoDB
ProductService --> Redis
PaymentService --> PostgreSQL

PaymentService --> Kafka
PaymentService --> Stripe
OrderService --> Kafka
Kafka --> OrderService
Kafka --> NotificationService
```

---

### Communication

```md
## Communication

- REST API → synchronous communication
- Kafka → asynchronous events