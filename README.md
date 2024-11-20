# Payment Processing Service

This project is a payment processing service built using **Spring Boot**. It integrates with an external **card service** to process payments and manage refunds. The system uses **RabbitMQ** for message queuing and **Dead Letter Queues (DLQ)** for handling failed messages with retry logic.

## Features

- **Payment Processing**: Processes payments and handles failed payment scenarios.
- **Refund Handling**: Manages refund operations for payments.
- **Dead Letter Queue**: Failed payment and refund messages are routed to a DLQ for retries.
- **Retry Mechanism**: Automatically retries failed messages with an increasing delay.
- **Caching**: Caches payment status to reduce database queries.

## Technologies Used

- **Spring Boot**: Backend framework.
- **RabbitMQ**: Message broker for handling queues.
- **Redis**: Cache for storing payment statuses.
- **Liquibase**: Database version control and migrations.
- **Lombok**: Reduces boilerplate code.

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java 11+**
- **Maven** or **Gradle** for build and dependency management.
- **RabbitMQ** server running on localhost:5672.
- **Redis** server running on localhost:6379.
- **MySQL** or other relational databases for storing payment data.
