✈️ Flight Ticket Management System

A scalable Flight Ticket Management System built using Spring Boot Microservices architecture.
The system handles customer registration, flight booking, notifications, and service-to-service communication using Kafka, with containerized deployment via Docker.

🚀 Features

Customer registration & authentication

Flight search and booking

Asynchronous notification system using Kafka

Email notifications on successful registration/booking

Microservices-based architecture

Dockerized deployment

Scalable and loosely coupled services

🏗️ Architecture Overview

The project follows a Microservices Architecture:

User Service

Handles customer registration and authentication

Flight Service

Manages flight details and availability

Booking Service

Handles flight ticket booking

Notification Service

Consumes Kafka events and sends email notifications

Kafka Broker

Event-driven communication between services

Database

MySQL (relational data)

MongoDB (notification data)

🛠️ Tech Stack

Backend: Java, Spring Boot

Microservices: Spring Cloud

Messaging Queue: Apache Kafka

Database: MySQL, MongoDB

Security: JWT Authentication

Build Tool: Maven

Containerization: Docker

Email Service: SMTP (Gmail)

📂 Project Structure
flight-ticket-management/
│
├── user-service/
├── flight-service/
├── booking-service/
├── notification-service/
├── docker-compose.yml
└── README.md
⚙️ Setup & Installation
1️⃣ Clone the repository
git clone https://github.com/your-username/flight-ticket-management.git
cd flight-ticket-management
2️⃣ Configure application properties

Update application.yml or application.properties in each service:

Database credentials

Kafka broker URL

Email SMTP configuration

🐳 Docker Deployment
Build Docker images
mvn clean package
docker build -t service-name .
Run using Docker Compose
docker-compose up -d

All services will start automatically.

📬 Kafka Flow (Notification Example)

Customer registers successfully

User Service publishes event to Kafka

Notification Service consumes the event

Data stored in MongoDB

Email notification sent to customer

🔐 Security

JWT-based authentication

Secured APIs using Spring Security

Token validation at API Gateway (if enabled)

📈 Future Enhancements

API Gateway implementation

Service discovery using Eureka

Payment gateway integration

Admin dashboard

Frontend using React/Angular

👨‍💻 Author

Om Narayan Mishra
B.Tech – Electrical & Electronics Engineering
Silicon University
