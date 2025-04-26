# Movie Ticket Booking Application

A backend project for booking movie tickets built using Java, Spring Boot, Spring Security (MFA + JWT), JPA, and MySQL.

## Features
- User Authentication (Login/Register with Multi-Factor Authentication)
- JWT-based authorization
- Book Movie Tickets
- Role-Based Access Control (Admin/User)
- Secure password storage with encryption
- Concurrent seat booking handling (Multithreading with Executor Service)

## Tech Stack
- Java 8
- Spring Boot
- MultiThreading
- Spring Security (JWT + MFA)
- Hibernate / JPA
- MySQL
- Maven

## Installation and Setup
1. Clone the repository
2. Set up environment variables for:
   - `spring.datasource.username`
   - `spring.datasource.password`
   - `spring.datasource.url`
   - `app.mfa.encryption.key`
3. Run `mvn clean install`
4. Start the application using your IDE or `mvn spring-boot:run`
5. Access the APIs through Postman

## Author
- Vaibhav Gangele

