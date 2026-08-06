# RetailFlow

## Overview

RetailFlow is a production-ready microservices-based retail and e-commerce platform built using Java 21, Spring Boot, React, and MySQL.

The goal of this project is to learn enterprise software development while building a real-world application that can be used to manage products, inventory, orders, billing, and AI-powered recommendations.

---

## Technology Stack

### Backend
- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Maven

### Frontend
- React

### Database
- MySQL

### Version Control
- Git
- GitHub

### DevOps (Future)
- Docker
- Kubernetes
- GitHub Actions

---

## Project Structure

```
RetailFlow/
│
├── docs/
├── frontend/
├── infrastructure/
├── scripts/
├── services/
├── README.md
└── .gitignore
```

---

## Development Status

Backend (Spring Boot 3.4.5, Java 21, H2 in-memory) - all services complete and tested:

- [x] Phase 1: product-service (port 8081) - Product + Brand CRUD, soft delete, search + pagination
- [x] Phase 2: warehouse-service (port 8082) - Warehouse CRUD
- [x] Phase 3: inventory-service (port 8083) - Stock in/out/adjust/transfer, StockMovement, low-stock, pagination
- [x] Phase 4: supplier-service (port 8084) - Supplier CRUD
- [x] Phase 5: purchase-service (port 8085) - Purchase orders with PO numbers, receive -> stock via inventory-service, supplier returns (RETURN_OUT)
- [x] Phase 6: customer-service (port 8086) - Customer CRUD
- [x] Phase 7: sales-service (port 8087) - Sales with SL numbers, complete -> stock out, customer returns -> stock in
- [x] Phase 8: billing-service (port 8088) - Bills with INV numbers, payments, printable bill via TVS printer abstraction
- [x] Phase 9: report-service (port 8089) - Sales, purchase, inventory, profit and low-stock reports (aggregates data over REST)
- [x] notification-service (port 8090) - EMAIL/SMS/in-app notifications behind a NotificationSender abstraction

Test coverage includes service unit tests, repository tests (@DataJpaTest), controller tests (@WebMvcTest), and context tests.

Next Phase:

- Frontend (React)
- Security (user-service with roles, permissions, JWT)
