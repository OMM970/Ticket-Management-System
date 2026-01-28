# ✈️ Flight Ticket Management System

A scalable **Flight Ticket Management System** built using **Spring Boot Microservices Architecture**.  
The system supports customer registration, flight booking, and asynchronous notifications using **Apache Kafka**, with **Docker-based deployment** for easy scalability.

---

## 🚀 Features

- Customer registration and authentication
- Flight search and ticket booking
- Asynchronous notification system using Kafka
- Email notifications on successful registration and booking
- Microservices-based architecture
- Dockerized deployment
- Secure REST APIs with JWT authentication

---

## 🏗️ Architecture Overview

The application follows a **microservices architecture**, enabling loose coupling and scalability.

### Microservices:

- **User Service**
  - Handles customer registration and authentication
- **Flight Service**
  - Manages flight details and availability
- **Booking Service**
  - Handles flight ticket booking
- **Notification Service**
  - Consumes Kafka events
  - Stores notification data
  - Sends email notifications
- **Kafka Broker**
  - Enables event-driven communication between services

---

## 🛠️ Tech Stack

- **Programming Language:** Java
- **Framework:** Spring Boot, Spring Security
- **Architecture:** Microservices
- **Message Broker:** Apache Kafka
- **Databases:**
  - MySQL (relational data)
  - MongoDB (notification storage)
- **Authentication:** JWT (JSON Web Token)
- **Build Tool:** Maven
- **Containerization:** Docker & Docker Compose
- **Email Service:** SMTP (Gmail)

---

## 📂 Project Structure
