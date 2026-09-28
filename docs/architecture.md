# RetailFlow architecture review

## Target shape

The browser communicates only with the API Gateway. The gateway validates JWTs and applies the coarse role policy. Domain services own their own data, register with Eureka, and obtain shared runtime configuration from Config Server.

Because this project intentionally excludes Kafka and RabbitMQ, checkout should use a synchronous orchestration service with explicit persisted states and compensating actions. A customer request should create an order first, reserve inventory, create the sale and bill, start the Razorpay payment, and only finalize stock after verified payment. Failed steps must be retryable and idempotent.

## Decisions already applied

- Only `ADMIN` and `USER` roles exist.
- Public registration is restricted to `USER`; administrator creation is environment-controlled.
- Gateway-issued access is JWT-based and stateless.
- Product catalogue reads are public; management operations require `ADMIN`.
- Razorpay order creation and verification require an authenticated user; manual payment management remains admin-only.
- Amounts sent to Razorpay are calculated from the stored bill and converted from INR to paise on the server.
- Payment success is accepted only after constant-time HMAC-SHA256 signature verification.
- Service-to-service HTTP clients resolve Eureka service names. Environment URL overrides remain available for diagnostics.

## Important remaining work

1. Add an `order-service` as the customer checkout boundary. Never let the frontend submit trusted prices, payment totals, role names, customer IDs, or warehouse IDs.
2. Associate each order and customer profile with the authenticated JWT subject. Users must only read their own orders and payments.
3. Replace the duplicated `product.stockQuantity` value with inventory-service as the single source of stock truth.
4. Add inventory reservation and idempotency keys before accepting concurrent checkout traffic.
5. Replace `count() + 1` invoice, sale, and purchase numbering with database sequences or UUID/ULID identifiers.
6. Stop swallowing remote-service failures in reporting and stock checks; use timeouts, bounded retries, circuit breaking, and visible partial-result metadata.
7. Replace H2 and `ddl-auto=update` with PostgreSQL/MySQL and Flyway migrations for deployment profiles.
8. Add service-level authorization or trusted service credentials. Gateway checks alone do not protect services if their ports are directly reachable.
9. Add OpenAPI aggregation, structured audit logs, correlation IDs, metrics, secret management, rate limiting, and backup/restore procedures.
10. Build the React frontend after the order contract is stable: catalogue, product details, cart, registration/login, address checkout, Razorpay Checkout, order history, and a separate admin layout.

## Recommended implementation order

1. Order aggregate, ownership rules, inventory reservation, and synchronous compensation.
2. Checkout-facing APIs and Razorpay test-mode end-to-end test.
3. Customer storefront and account pages.
4. Admin portal for the existing management APIs.
5. Deployment database profiles, migrations, observability, security hardening, and end-to-end tests.
