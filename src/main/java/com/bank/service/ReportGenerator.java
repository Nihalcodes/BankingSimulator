package com.bank.service;

import com.bank.model.Account;
import com.bank.ConfigLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

public class ReportGenerator {
    private static final String DB_URL = ConfigLoader.get("db.url");
    private static final String DB_USER = ConfigLoader.get("db.user");
    private static final String DB_PASS = ConfigLoader.get("db.password");

    public void generateAccountSummary(Account account) {
        String dir = "reports";
        new File(dir).mkdirs();
        String filename = dir + "/account_" + account.getAccountId() + ".txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=== BANKING SIMULATOR - ACCOUNT SUMMARY ===");
            writer.println("Account ID: " + account.getAccountId());
            writer.println("Owner: " + account.getOwnerName());
            writer.println("Current Balance: $" + account.getBalance());
            writer.println("Report Generated: " + LocalDateTime.now());
            writer.println("\n=== TRANSACTION HISTORY ===");

            String sql = "SELECT type, amount, from_account, to_account, status, timestamp " +
                    "FROM transactions " +
                    "WHERE from_account = ? OR to_account = ? " +
                    "ORDER BY timestamp DESC LIMIT 10";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, account.getAccountId());
                stmt.setInt(2, account.getAccountId());
                ResultSet rs = stmt.executeQuery();

                if (!rs.isBeforeFirst()) {
                    writer.println("No transactions found.");
                } else {
                    while (rs.next()) {
                        String type = rs.getString("type");
                        BigDecimal amount = rs.getBigDecimal("amount");
                        String status = rs.getString("status");
                        Timestamp ts = rs.getTimestamp("timestamp");

                        String desc;
                        if ("DEPOSIT".equals(type)) {
                            desc = String.format("DEPOSIT: +$%.2f", amount);
                        } else if ("WITHDRAWAL".equals(type)) {
                            desc = String.format("WITHDRAWAL: -$%.2f", amount);
                        } else {
                            int from = rs.getInt("from_account");
                            int to = rs.getInt("to_account");
                            if (account.getAccountId() == from) {
                                desc = String.format("TRANSFER OUT: -$%.2f → %d", amount, to);
                            } else {
                                desc = String.format("TRANSFER IN: +$%.2f ← %d", amount, from);
                            }
                        }
                        writer.printf("[%s] %s [%s]%n", ts, desc, status);
                    }
                }
            }

            writer.println("\n==========================================");
            System.out.println("✅ Report saved: " + filename);
        } catch (Exception e) {
            System.out.println("❌ Failed to generate report: " + e.getMessage());
        }
    }
}