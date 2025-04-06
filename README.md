
# Wallet Service

This project implements a **digital wallet service** capable of handling user wallets, processing transactions (deposit, withdraw, and transfer), and integrating with a message queue (RabbitMQ) for asynchronous transaction processing.

---

## 🚀 How to Run

1. **Clone the repository**:
   ```bash
   cd wallet-service
   ```

2. **Start infrastructure (RabbitMQ)**:
   ```bash
   docker-compose up -d
   ```
    When running the project with `docker-compose up -d`, RabbitMQ will be available locally.

   **RabbitMQ Management Console**:  
     Access it via your browser at: [http://localhost:15672/](http://localhost:15672/)

   **Default Credentials**:
     - Username: guest
     - Password: guest

   **From the RabbitMQ console, you can:**
    - Monitor queues and messages
    - Check the dead-letter queue (DLQ)
    - Publish test messages manually


3. **Run the application**:
   - Requirements:
     - Java 17
     - Gradle

  - Build the Project:
    ```bash
    ./gradlew clean build
    ```
  - Run locally:
    ```bash
    ./gradlew bootRun
    ```

4. **Access the application**:
   - API: [http://localhost:9090](http://localhost:9090)
   - Swagger Documentation: [http://localhost:9090](http://localhost:9090/swagger-ui/index.html#/)


5. **Database - H2 Console**:
   - API: [http://localhost:9090/h2-console/](http://localhost:9090/h2-console/)
   - JDBC URL: jdbc:h2:mem:walletdb
   - Username: sa


6. **Metrics**:
    - Metrics: [http://localhost:9090/actuator/metrics](http://localhost:9090/actuator/metrics)
    - Messages Consumed Success: [http://localhost:9090/actuator/metrics/transaction.messages.consumed.success](http://localhost:9090/actuator/metrics/transaction.messages.consumed.success)
    - Messages Consumed Failure: [http://localhost:9090/actuator/metrics/transaction.messages.consumed.failure](http://localhost:9090/actuator/metrics/transaction.messages.consumed.failure)

---

## 📚 Features

- **Create Wallet**: Allow the creation of wallets for users.
- **Retrieve Balance**: Retrieve the current balance of a user's wallet.
- **Retrieve Historical Balance**: Retrieve the balance of a user's wallet at a specific point in the past.
- **Deposit, Withdraw, Transfer**: Manage wallet transactions via asynchronous messaging.
- **Asynchronous Transaction Processing**: Transactions are sent to RabbitMQ and processed by a consumer.
- **Dead-Letter Queue (DLQ)**: Failed transactions are automatically sent to a DLQ.
- **Monitoring**: Basic metrics exposed through Spring Boot Actuator (messages consumed, messages failed).
- **Tracing**: `traceId` and `spanId` are included in logs for better traceability across the system.
- **Swagger UI**: Full documentation of controllers and the ability to test endpoints.
- **Caching**: Simple caching mechanism for wallet balance.

---

## 🎯 Design Choices
### Architecture
- **Spring Boot + Java 17**: Modern, robust, and production-ready backend.
- **RabbitMQ Integration**: for message-driven communication.
- **Dead-Letter Queue (DLQ)**: Guarantees that failed messages are not lost and can be investigated later.
- **Retry Mechanism**: Messages are retried up to 3 times before moving to the DLQ.
- **Wallet Balance Reservation**: Withdraw and transfer transactions use a reservation mechanism to prevent overspending before actual processing.
- **H2 in-memory database**: For fast and simple persistence during development.
- **Scheduler**: To automatically cancel expired`PENDING`transaction.

---

## 🔄 Transaction Processing Flow

| Step | What Happens | Result |
|:----:|:--------------|:-------|
| 1 | User initiates a transaction request | Creates a transaction with status `PENDING` and generates a transaction code (UUID) |
| 2 | User confirms the transaction | Updates status to `PROCESSING` and reserves the amount in the wallet (only for `TRANSFER` and `WITHDRAW` transactions) |
| 3 | Sends the transaction to the queue (RabbitMQ) | Message is sent for asynchronous processing |
| 4 | Consumer processes the transaction | Applies the operation and updates balance/history |
| 5 | Success | Marks the transaction as `COMPLETED` and saves to transaction history |
| 6 | Error | Marks the transaction as `FAILED` and releases the reserved balance (only for `TRANSFER` and `WITHDRAW` transactions) |

**Note**:  
- `TRANSFER` and `WITHDRAW` transactions use balance reservation (`reserveAmount`) during the confirmation step.  
- `DEPOSIT` transactions do not reserve balance; they are directly processed when consumed.

---

## ⚖️ Trade-offs due Time Constraints

- **Security**: No authentication or authorization mechanisms were implemented.
- **No Database Persistence**: The service uses an in-memory database (H2) for simplicity. In a production scenario, a relational database like PostgreSQL would be preferable.
- **Basic Monitoring**: Metrics are collected internally but not exported to a full observability stack like Prometheus and Grafana to save time.
- **Simplified Error Handling**: Basic exception handling is implemented; a more robust error model could be added.
- **No DLQ reprocess**: While failed messages are sent to a DLQ, no retry or reprocessing mechanism from the DLQ was implemented yet.
- **Single Service**: This project assumes a monolithic approach for easier setup, but in real-world cases, wallet management and transaction processing could be separated into microservices.

Despite these trade-offs, the service is fully functional, resilient to processing errors, and easily extensible for future improvements.
