package com.bank.service;

import com.bank.exception.AccountNotFoundException;
import com.bank.exception.DuplicateAccountException;
import com.bank.model.Account;
import com.bank.ConfigLoader;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountService {
    private static final String DB_URL = ConfigLoader.get("db.url");
    private static final String DB_USER = ConfigLoader.get("db.user");
    private static final String DB_PASS = ConfigLoader.get("db.password");

    // Admin credentials (hardcoded as requested)
    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin";

    public Account createAccount(String ownerName, String email, String password, BigDecimal initialBalance) throws SQLException, DuplicateAccountException {
        if (accountExists(email)) {
            throw new DuplicateAccountException("Account with email '" + email + "' already exists.");
        }

        String sql = "INSERT INTO accounts (owner_name, email, password, balance) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, ownerName);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setBigDecimal(4, initialBalance != null ? initialBalance : BigDecimal.ZERO);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new Account(id, ownerName, email, password, initialBalance);
                }
            }
            throw new SQLException("Failed to create account");
        }
    }

    private boolean accountExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM accounts WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public Account authenticate(String email, String password) throws SQLException, AccountNotFoundException {
        String sql = "SELECT account_id, owner_name, email, password, balance FROM accounts WHERE email = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Account(
                        rs.getInt("account_id"),
                        rs.getString("owner_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBigDecimal("balance")
                );
            }
            else {
                throw new AccountNotFoundException("No account found for email: " + email);
            }
        }
    }

    public boolean authenticateAdmin(String email, String password) {
        return ADMIN_EMAIL.equals(email) && ADMIN_PASSWORD.equals(password);
    }

    public Account findAccountById(int accountId) throws SQLException, AccountNotFoundException {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Account(
                        rs.getInt("account_id"),
                        rs.getString("owner_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBigDecimal("balance")
                );
            }
            else {
                throw new AccountNotFoundException("Account ID " + accountId + " not found.");
            }
        }
    }

    public void updateBalance(int accountId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setInt(2, accountId);
            stmt.executeUpdate();
        }
    }

    public List<Account> getAllAccounts() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id, owner_name, email, password, balance FROM accounts";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                accounts.add(new Account(
                        rs.getInt("account_id"),
                        rs.getString("owner_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBigDecimal("balance")
                ));
            }
        }
        return accounts;
    }

    public List<Map<String, Object>> getAllTransactions() throws SQLException {
        List<Map<String, Object>> transactions = new ArrayList<>();
        String sql = "SELECT id, from_account, to_account, amount, type, status, timestamp " +
                "FROM transactions ORDER BY timestamp DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> tx = new HashMap<>();
                tx.put("id", rs.getString("id"));
                tx.put("from", rs.getObject("from_account"));
                tx.put("to", rs.getObject("to_account"));
                tx.put("amount", rs.getBigDecimal("amount"));
                tx.put("type", rs.getString("type"));
                tx.put("status", rs.getString("status"));
                tx.put("timestamp", rs.getTimestamp("timestamp"));
                transactions.add(tx);
            }
        }
        return transactions;
    }
    public List<Account> getOtherAccounts(int excludeAccountId) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id, owner_name, email, password, balance FROM accounts WHERE account_id != ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, excludeAccountId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                accounts.add(new Account(
                        rs.getInt("account_id"),
                        rs.getString("owner_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBigDecimal("balance")
                ));
            }
        }
        return accounts;
    }
}