# RetailFlow

RetailFlow is a learning-focused e-commerce system structured as Spring Boot microservices. It has two roles:

- `USER` — self-registers, browses products, checks out, and pays for orders.
- `ADMIN` — manages the catalogue, warehouses, inventory, suppliers, purchasing, customers, sales, billing, reports, notifications, and users.

Prices and payments use Indian rupees (INR). The project deliberately does not use Docker, Kafka, or RabbitMQ.

## Platform

- Java 21, Spring Boot 3.4.13, Spring Cloud 2024.0.3
- Eureka service discovery on port `8761`
- Spring Cloud Config Server on port `8888`
- Spring Cloud Gateway on port `8080`
- JWT authentication in `auth-service` on port `8091`
- Razorpay order creation and server-side signature verification
- React + TypeScript + Vite frontend (next implementation phase)
- H2 for local learning; PostgreSQL or MySQL plus Flyway is recommended before deployment

All browser traffic must enter through the gateway. Service-to-service clients use Eureka logical service names rather than fixed localhost ports.

## Backend modules

| Module | Port | Responsibility |
|---|---:|---|
| discovery-server | 8761 | Eureka registry |
| config-server | 8888 | Central configuration |
| api-gateway | 8080 | Routing, CORS, JWT verification, role policy |
| product-service | 8081 | Products and brands |
| warehouse-service | 8082 | Warehouses |
| inventory-service | 8083 | Stock and movements |
| supplier-service | 8084 | Suppliers |
| purchase-service | 8085 | Purchases and supplier returns |
| customer-service | 8086 | Customer records |
| sales-service | 8087 | Sales and customer returns |
| billing-service | 8088 | Bills, payments, Razorpay |
| report-service | 8089 | Operational reports |
| notification-service | 8090 | Notification records and senders |
| auth-service | 8091 | Registration, login, JWTs, users |

## Local build

```powershell
mvn test
```

Start services in this order: discovery server, config server, API gateway, then the domain services. No container runtime is required.

Customer registration and login:

```text
POST http://localhost:8080/api/v1/auth/register
POST http://localhost:8080/api/v1/auth/login
```

Public self-registration always creates `USER`. It cannot create an administrator. To bootstrap an administrator, set `RETAILFLOW_ADMIN_EMAIL` and `RETAILFLOW_ADMIN_PASSWORD` before starting `auth-service`.

JWT signing uses `JWT_SECRET`; use the same secret for `auth-service` and the gateway and supply at least 32 UTF-8 bytes. The checked-in value is development-only.

For Razorpay test mode, set `RAZORPAY_KEY_ID` and `RAZORPAY_KEY_SECRET` for `billing-service`. Never place the secret in frontend code. The backend creates an INR order and verifies the returned HMAC signature before recording a successful payment.

## Current status

The service registry, central configuration, gateway security, customer self-registration, JWT login, and Razorpay backend foundation are implemented. The existing backend regression suite plus focused auth/payment tests pass.

The next implementation phase is the customer order/checkout boundary and React frontend. See [architecture.md](docs/architecture.md) for the reviewed risks and recommended sequence.
