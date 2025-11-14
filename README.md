# 💰 Banking Transaction Simulator (Java)

A lightweight **console-based banking simulator** built using **Core Java**, **MySQL**, and **JDBC**. It supports creating accounts, performing deposits, withdrawals, transfers, viewing reports, and receiving low-balance email alerts. Designed as an academic project for learning essential Java + database concepts.

---

## 🚀 Features

* Create customer accounts (email + password login)
* Deposit, withdraw, and transfer money
* Auto-logged transactions stored in MySQL
* Low-balance email alerts (Gmail SMTP)
* Admin dashboard to view all accounts & transactions
* Generates text-based account reports

---

## 🛠️ Tech Stack

* **Java 17** (Core Java, no frameworks)
* **MySQL 8+** with JDBC
* **JavaMail API** for SMTP alerts
* **IntelliJ IDEA Community**

---

## 📦 Project Setup

1. **Clone the project:**

   ```bash
   ```

git clone https://github.com/Nihalcodes/BankingSimulator.git
cd BankingSimulator

````
2. **Configure MySQL:**
   - Install MySQL or XAMPP
   - The app auto-creates DB/tables on first run

3. **Add `config.properties` in project root:**
   ```properties
db.url=jdbc:mysql://localhost:3306/
db.user=root
db.password=your_mysql_password
smtp.email=your_email@gmail.com
smtp.password=your_gmail_app_password
````

4. **Add required libraries:**

   * `mysql-connector-java.jar`
   * `javax.mail.jar`

5. **Run the app:** Open `Main.java` → Run.

---

## 📘 Basic Usage

### Customer

* Create account
* Login with email/password
* Deposit / Withdraw / Transfer funds
* Generate account report

### Admin

Login as admin and view all accounts and all transactions.

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
* Prepared statements prevent SQL injection
* Email alerts require a Gmail **App Password**

---

## 📜 License

This project is licensed under the **MIT License**.

---

### ✔ Designed to be simple, clean, and easy to understand while demonstrating real-world banking logic using Core Java.
