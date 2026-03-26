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
flight-ticket-management/
│
├── user-service/
├── flight-service/
├── booking-service/
├── notification-service/
├── docker-compose.yml
└── README.md


---

## ⚙️ Installation & Setup

### 1️⃣ Clone the Repository
git clone https://github.com/your-username/flight-ticket-management.git
cd flight-ticket-management
2️⃣ Configure Application Properties

Update the following configurations in each microservice:

Database URL, username, and password

Kafka broker configuration

SMTP email credentials

JWT secret key

🐳 Docker Deployment
Build all services
mvn clean package
Run the application using Docker Compose
docker-compose up -d

All microservices will start automatically in detached mode.

📬 Kafka Event Flow (Notification Example)

Customer registers successfully

User Service publishes an event to Kafka

Notification Service consumes the event

Notification data is stored in MongoDB

Email notification is sent to the customer

🔐 Security

JWT-based authentication

Spring Security for API protection

Secured endpoints for authorized users only

📈 Future Enhancements

API Gateway implementation

Service discovery using Eureka

Payment gateway integration

Frontend using React or Angular

Admin dashboard for flight and booking management ............

👨‍💻 Author

Om Narayan Mishra
B.Tech – Electrical & Electronics Engineering
Silicon University
