package com.bank.util;

import java.math.BigDecimal;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Logger;

public class TransactionLogger {
    private static final String URL = "jdbc:mysql://localhost:3306/banking_simulator";
    private static final String USER = "root";
    private static final String PASSWORD = "Nihal";
    private static final Logger logger = Logger.getLogger(TransactionLogger.class.getName());

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void logTransaction(Integer fromAccount, Integer toAccount, BigDecimal amount,
                               String type, String status) {
        String sql = "INSERT INTO transactions (id, from_account, to_account, amount, type, status, timestamp) " +
                "VALUES (?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setObject(2, fromAccount);
            stmt.setObject(3, toAccount);
            stmt.setBigDecimal(4, amount);
            stmt.setString(5, type);
            stmt.setString(6, status);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.severe("Failed to log transaction: " + e.getMessage());
        }
    }
}