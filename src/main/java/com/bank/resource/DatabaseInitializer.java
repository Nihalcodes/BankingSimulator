package com.bank.resource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class DatabaseInitializer {
    private static final Logger logger = Logger.getLogger(DatabaseInitializer.class.getName());

    // Use root credentials (no DB name in URL)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Nihal"; // ⚠️ Update if needed

    private static final String DB_NAME = "banking_simulator";

    private static final String CREATE_DB_SQL = "CREATE DATABASE IF NOT EXISTS " + DB_NAME
            + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";

    private static final String CREATE_ACCOUNTS_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS accounts (" +
                    "    account_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "    owner_name VARCHAR(100) NOT NULL," +
                    "    email VARCHAR(100) NOT NULL UNIQUE," +
                    "    password VARCHAR(255) NOT NULL," +
                    "    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00" +
                    ")";

    private static final String CREATE_TRANSACTIONS_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS transactions (" +
                    "    id VARCHAR(36) PRIMARY KEY," +
                    "    from_account INT," +
                    "    to_account INT," +
                    "    amount DECIMAL(15,2) NOT NULL," +
                    "    type VARCHAR(20) NOT NULL," +
                    "    status VARCHAR(10) NOT NULL," +
                    "    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "    FOREIGN KEY (from_account) REFERENCES accounts(account_id)," +
                    "    FOREIGN KEY (to_account) REFERENCES accounts(account_id)" +
                    ")";

    private static final String SET_AUTO_INCREMENT_SQL =
            "ALTER TABLE accounts AUTO_INCREMENT = 1001";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.severe("MySQL JDBC Driver not found: " + e.getMessage());
            throw new RuntimeException("Failed to load MySQL driver", e);
        }
    }

    /**
     * Call this method at the start of your application (e.g., in Main.main())
     */
    public static void initializeDatabase() {
        logger.info("🔧 Initializing database...");

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // 1. Create database
            stmt.execute(CREATE_DB_SQL);
            logger.info("✅ Database '" + DB_NAME + "' is ready.");

            // 2. Switch to the database
            stmt.execute("USE " + DB_NAME);

            // 3. Create tables
            stmt.execute(CREATE_ACCOUNTS_TABLE_SQL);
            logger.info("✅ Table 'accounts' is ready.");

            stmt.execute(CREATE_TRANSACTIONS_TABLE_SQL);
            logger.info("✅ Table 'transactions' is ready.");

            // 4. Ensure account IDs start from 1000
            stmt.execute(SET_AUTO_INCREMENT_SQL);
            logger.info("✅ Account ID auto-increment set to start from 1000.");

        } catch (SQLException e) {
            logger.severe("❌ Database initialization failed: " + e.getMessage());
            throw new RuntimeException("Unable to initialize database", e);
        }

        logger.info("✅ Database setup completed successfully.");
    }
}