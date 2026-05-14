# 🏦 LendingPro – Loan Management & Repayment Platform

A backend fintech application built using **Java 17, Spring Boot 3.2, and MySQL** to manage the complete loan lifecycle including customer onboarding, KYC verification, loan application processing, EMI scheduling, repayment tracking, reporting, and audit logging.

---

# 🚀 Tech Stack

- Java 17
- Spring Boot 3.2
- Spring Security + JWT
- Spring Data JPA (Hibernate)
- MySQL 8
- JasperReports (PDF Reporting)
- Apache POI (Excel Reporting)
- Maven
- JUnit & Mockito
- Swagger / OpenAPI

---

# 🏗 Architecture

- Modular Monolith Architecture
- Layered Backend Design:
  - Controller Layer
  - Service Layer
  - Repository Layer
  - DTO Mapping Layer
- RESTful API Design
- Stateless JWT Authentication
- Scheduler-based Background Jobs

---

# 🔐 Core Modules

## 👤 Authentication & Authorization
- JWT-based authentication
- Role-based access control (USER / ADMIN / AUDITOR)
- BCrypt password encryption
- Secure API access using Spring Security

---

## 👨‍💼 Customer Management
- Customer profile management
- Employment and income details
- Address management
- KYC linkage with customer records

---

## 📄 KYC Management
- KYC document upload
- Verification workflow
- Admin approval/rejection process
- KYC status tracking

---

## 💰 Loan Management
- Loan product creation and management
- Loan application processing
- Rule-based loan eligibility calculation
- Loan approval and rejection workflow

---

## 🏦 Loan Account Management
- Automatic loan account creation after approval
- Loan disbursement tracking
- Outstanding balance calculation
- Loan lifecycle status management

---

## 📊 EMI Management
- Automatic EMI schedule generation
- Principal and interest calculation
- EMI due date tracking
- Remaining balance tracking

---

## 💳 Repayment Processing
- EMI payment processing
- Partial and full repayment handling
- Payment history maintenance
- Transaction tracking

---

## ⚠️ Penalty Management
- Overdue EMI detection
- Late payment penalty calculation
- Loan delinquency tracking
- Status updates for overdue loans

---

## 📑 Reporting & Documents
- Loan statement PDF generation
- EMI schedule reports
- Payment receipt generation
- Monthly collection reports
- Overdue loan reports
- Administrative reporting APIs

---

## 🔔 Notification Tracking
- Loan approval notifications
- EMI due reminders
- Payment success notifications
- Event-based notification records

---

## 🧾 Audit Logging
- Admin activity tracking
- Loan lifecycle audit logs
- KYC verification logs
- Approval and repayment activity logs

---

# 🔄 Application Workflow

## Customer Workflow
Register → Login → Profile Setup → KYC Upload → Loan Application → Approval → EMI Generation → Repayment → Loan Closure

## Admin Workflow
Login → KYC Verification → Loan Approval → EMI Monitoring → Reporting → Audit Review

---

# 📊 API Documentation

Swagger UI:
http://localhost:8080/swagger-ui/index.html

OpenAPI Docs:
http://localhost:8080/v3/api-docs

---

# 🐳 Docker Support

## Build Docker Image

```bash
docker build -t loan-management .
```

## Run Docker Container

```bash
docker run -d --name loan-app -p 8082:8080 ^
-e DB_URL="jdbc:mysql://host.docker.internal:3306/loandb?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC" ^
-e DB_USERNAME="YOUR_DB_USERNAME" ^
-e DB_PASSWORD="YOUR_DB_PASSWORD" ^
-e JWT_SECRET="YOUR_SECRET_KEY" ^
loan-management
```

## Verify Running Container

```bash
docker ps
```

## Access Swagger from Docker

http://localhost:8082/swagger-ui/index.html

---

# 🗄 Database Tables

- users
- customer_profile
- kyc_documents
- loan_products
- loan_applications
- loan_accounts
- emi_schedule
- payments
- penalties
- notifications
- audit_logs

---

# ⏱ Scheduler Jobs

- Daily EMI Reminder Job
- Overdue Loan Detection Job
- Penalty Calculation Job
- Monthly Report Generation Job
- Automatic Loan Closure Job

---

# 📌 Project Highlights

- 38+ REST APIs
- End-to-end loan lifecycle management
- JWT-secured backend APIs
- Real-world fintech workflow simulation
- Clean modular backend architecture
- Scheduler-based automation
- PDF & Excel reporting support
- Dockerized Spring Boot application
- Swagger/OpenAPI integration
- JasperReports integration

---

# 🧪 Testing

- Unit testing using JUnit 5 & Mockito
- API testing using Postman
- Swagger API validation

---

# ⚙️ Environment Variables

| Variable | Description |
|----------|-------------|
| DB_URL | MySQL Database URL |
| DB_USERNAME | Database Username |
| DB_PASSWORD | Database Password |
| JWT_SECRET | JWT Secret Key |

---

# 🔗 GitHub Repository

https://github.com/mahabs2002/lendingpro-loan-management-platform

---

# 🧑‍💻 Author

**Mahalakshmi**  
Java Backend Developer | 1.8 Years Experience

---

# ⚡ Project Status

✔ Backend Development Completed  
✔ Swagger Documentation Integrated  
✔ Dockerized Application  
✔ GitHub Repository Configured  
✔ Ready for AWS Deployment
