# 💰 Banking Transaction Simulator (Java)

A lightweight **console-based banking simulator** built using **Core Java**, **MySQL**, and **JDBC**. It allows users to create accounts, perform banking operations (deposit, withdrawal, transfer), view reports, and receive automatic low-balance alerts. Designed as an academic project to demonstrate foundational Java + database concepts.

---

## 🚀 **Key Features (Highlighted)**

### 🔐 **Account Management**

* Create customer accounts (email + password login)
* Secure login system for users and admin

### 💸 **Core Banking Operations**

* Deposit funds
* Withdraw funds
* Transfer money between accounts
* Real-time balance updates

### 📝 **Transaction Logging & Reporting**

* All transactions stored in MySQL
* Auto-generated text-based account reports
* Admin view for all accounts & all transactions

### 📧 **Email Notifications**

* Low-balance alerts via Gmail SMTP
* Configurable alert threshold

---

## 🛠️ Tech Stack

* **Java 17** (Core Java — no frameworks)
* **MySQL 8+** with JDBC
* **JavaMail API** for email alerts
* **IntelliJ IDEA Community Edition**

---

## 📦 Project Setup

### 1. Clone the project

```bash
git clone https://github.com/Nihalcodes/BankingSimulator.git
cd BankingSimulator
```

### 2. Configure MySQL

* Install MySQL or XAMPP
* Database + tables auto-create on first run

### 3. Create `config.properties` in the project root

```properties
db.url=jdbc:mysql://localhost:3306/
db.user=root
db.password=your_mysql_password
smtp.email=your_email@gmail.com
smtp.password=your_gmail_app_password
```

### 4. Add required libraries

* `mysql-connector-java.jar`
* `javax.mail.jar`

### 5. Run the app

Open `Main.java` → **Run**

---

## 📘 Basic Usage

### 👤 Customer

* Create account
* Login with email/password
* Deposit / Withdraw / Transfer funds
* Generate account report

### 🛡️ Admin

* Login as admin
* View **all accounts**
* View **all transactions**

---

## 📁 Project Structure (Simplified)

```
BankingSimulator/
├── src/com/bank/
│   ├── Main.java
│   ├── service/
│   ├── model/
│   ├── exception/
│   └── util/
├── resources/schema.sql
├── reports/ (auto-generated)
├── lib/ (JDBC + Mail JARs)
├── .gitignore
└── LICENSE
```

---

## 🔐 Security Notes

* `config.properties` is **ignored in Git**
* Prepared statements protect against SQL injection
* Email alerts require a **Gmail App Password**

---

## 📜 License

This project is licensed under the **MIT License**.

---

### ✔ Clean, simple, and professional — designed for learning real-world banking logic using Core Java.
