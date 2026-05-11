# 🏦 Loan Management & Repayment Platform

A backend fintech system built using **Java 17 + Spring Boot 3.2 + MySQL**, designed to automate loan lifecycle management including application, approval, EMI scheduling, repayment, reporting, and audit tracking.

---

## 🚀 Tech Stack

- Java 17
- Spring Boot 3.2
- Spring Security + JWT
- Spring Data JPA (Hibernate)
- MySQL 8
- JasperReports (PDF Reporting)
- Apache POI (Excel Reports)
- Maven
- JUnit & Mockito
- Swagger UI

---

## 🏗 Architecture

- Modular Monolith Backend
- Layered Architecture:
    - Controller
    - Service
    - Repository
    - DTO
- Stateless Authentication (JWT)
- Scheduler-based automation

---

## 🔐 Key Features

### 👤 Authentication & Authorization
- JWT-based login system
- Role-based access: USER / ADMIN / AUDITOR
- BCrypt password encryption

---

### 👨‍💼 Customer Module
- Profile management
- Employment & income details
- Address & KYC linkage

---

### 📄 KYC Module
- Document upload & verification
- Admin approval/rejection workflow

---

### 💰 Loan Management
- Loan product creation (Admin)
- Loan application system
- Eligibility score engine
- Approval / rejection workflow

---

### 🏦 Loan Account
- Auto account creation after approval
- Loan disbursement tracking
- Outstanding balance management

---

### 📊 EMI System
- Automatic EMI schedule generation
- Principal + Interest breakdown
- Due date tracking

---

### 💳 Repayment Module
- EMI payment processing
- Partial/full payment support
- Payment history tracking

---

### ⚠️ Penalty System
- Overdue detection
- Late fee calculation
- Loan status updates

---

### 📑 Reporting (JasperReports)
- Loan statement PDF
- EMI schedule PDF
- Payment receipts
- Monthly collection reports
- Overdue reports
- Admin dashboards

---

### 🔔 Notification System
- Loan approval alerts
- EMI due reminders
- Payment success notifications

---

### 🧾 Audit Logging
- Admin actions tracking
- Loan lifecycle tracking
- KYC and approval logs

---

## 🔄 Workflow

### Customer Flow
Register → Login → Profile → KYC → Loan Apply → Approval → EMI → Payment → Closure

### Admin Flow
Login → KYC Verify → Approve Loans → Monitor EMIs → Reports → Audit Logs

---

## 📊 API Documentation (Swagger)
http://localhost:8080/swagger-ui/index.html


---

## 🗄 Database Tables

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

## ⏱ Scheduler Jobs

- EMI Reminder Job (Daily)
- Overdue Detection Job (Daily)
- Penalty Calculation Job (Daily)
- Monthly Reports Generation
- Auto Loan Closure Job

---

## 📌 Highlights

- 40+ REST APIs
- End-to-end loan lifecycle system
- Production-style backend architecture
- Real-world fintech workflow simulation
- Clean modular design

---

## 🧑‍💻 Author

Backend Developer: Mahalakshmi  
Experience: 1.8 Years (Java Backend)

---

## ⚡ Status

✔ Completed Backend  
✔ Swagger Integrated  
✔ GitHub Pushed  
✔ Ready for AWS Deployment