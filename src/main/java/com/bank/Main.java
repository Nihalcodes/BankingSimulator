package com.bank;

import com.bank.exception.AccountNotFoundException;
import com.bank.exception.DuplicateAccountException;
import com.bank.exception.InsufficientFundsException;
import com.bank.model.Account;
import com.bank.service.*;
import com.bank.resource.DatabaseInitializer;
import com.bank.util.TransactionLogger;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static AccountService accountService;
    private static TransactionService txService;
    private static ReportGenerator reportGen;
    private static AlertService alertService;
    private static final Scanner scanner = new Scanner(System.in);
    private static Account currentAccount = null;

    public static void main(String[] args) {

        DatabaseInitializer.initializeDatabase();

        initializeServices();
        while (true) {
            if (currentAccount == null) {
                showLoginMenu();
            } else {
                showUserMenu();
            }
        }
    }

    private static void initializeServices() {
        accountService = new AccountService();
        TransactionLogger loggerUtil = new TransactionLogger();
        txService = new TransactionService(accountService, loggerUtil);
        reportGen = new ReportGenerator();
        alertService = new AlertService();
    }

    private static void showLoginMenu() {
        logger.info("\n=== WELCOME TO BANKING SIMULATOR ===\n1. Login\n2. Create Account\n0. Exit\nChoose: ");
        int choice = getIntInput();
        switch (choice) {
            case 1 -> login();
            case 2 -> createAccount();
            case 0 -> {
                logger.info("👋 Goodbye!");
                System.exit(0);
            }
            default -> logger.warning("❌ Invalid choice.");
        }
    }

    private static void login() {
        logger.info("Email: ");
        String email = scanner.nextLine();
        logger.info("Password: ");
        String password = scanner.nextLine();

        if (accountService.authenticateAdmin(email, password)) {
            logger.info("✅ Logged in as ADMIN");
            showAdminMenu();
            return;
        }

        try {
            Account acc = accountService.authenticate(email, password);
            if (acc != null) {
                currentAccount = acc;
                logger.info("✅ Logged in as: " + acc.getOwnerName());
            } else {
                logger.warning("❌ Invalid email or password.");
            }
        }
        catch (AccountNotFoundException e) {
            logger.severe("❌ Account not found: " + e.getMessage());
        }
        catch (SQLException e) {
            logger.severe("Login error: " + e.getMessage());
        }
    }

    private static void createAccount() {
        logger.info("Owner name: ");
        String name = scanner.nextLine();
        logger.info("Email: ");
        String email = scanner.nextLine();
        logger.info("Password: ");
        String password = scanner.nextLine();
        logger.info("Initial balance: ");
        BigDecimal bal = getBigDecimalInput();

        try {
            Account acc = accountService.createAccount(name, email, password, bal);
            logger.info("✅ Account created! Your ID: " + acc.getAccountId());
        }
        catch (DuplicateAccountException e) {
            logger.severe("❌ Duplicate Account found: " + e.getMessage());
        }
        catch (SQLException e) {
            logger.severe("❌ Account creation failed: " + e.getMessage());
        }
    }

    private static void showAdminMenu() {
        while (true) {
            logger.info("\n=== 🔐 ADMIN DASHBOARD === \n1. View All Accounts\n2. View All Transactions (Latest First)\n3. Logout\nChoose: ");

            int choice = getIntInput();

            try {
                switch (choice) {
                    case 1 -> viewAllAccounts();
                    case 2 -> viewAllTransactions();
                    case 3 -> {
                        logger.info("✅ Admin logged out.");
                        return;
                    }
                    default -> logger.warning("❌ Invalid choice.");
                }
            } catch (Exception e) {
                logger.severe("⚠️ Admin error: " + e.getMessage());
            }
        }
    }

    private static void showUserMenu() {
        logger.info("\n=== HI, " + currentAccount.getOwnerName() + " ==="+"\n1. Deposit\n2. Withdraw\n3. Transfer\n4. Check Balance Alerts\n5. Generate Report\n6. View Account\n7. Logout\nChoose: ");
        int choice = getIntInput();

        try {
            switch (choice) {
                case 1 -> deposit();
                case 2 -> withdraw();
                case 3 -> transfer();
                case 4 -> checkAlerts();
                case 5 -> generateReport();
                case 6 -> viewAccount();
                case 7 -> logout();
                default -> logger.warning("❌ Invalid choice.");
            }
        } catch (Exception e) {
            logger.severe("⚠️ Error: " + e.getMessage());
        }
    }

    private static void logout() {
        logger.info("✅ Logged out.");
        currentAccount = null;
    }

    private static void viewAccount() {
        logger.info(currentAccount.toString());
    }

    private static void deposit() throws Exception {
        try{
            logger.info("Amount: ");
            BigDecimal amt = getBigDecimalInput();
            txService.deposit(currentAccount.getAccountId(), amt);
            currentAccount = accountService.findAccountById(currentAccount.getAccountId());
            logger.info("✅ Deposit successful. New balance: $" + currentAccount.getBalance());
        }
        catch (AccountNotFoundException e) {
            logger.severe("❌ Account not found: " + e.getMessage());
        } catch (InsufficientFundsException e) {
            logger.warning("⚠️ " + e.getMessage());
        } catch (Exception e) {
            logger.severe("❌ Unexpected error: " + e.getMessage());
        }

    }

    private static void withdraw() throws Exception {
        try{
            logger.info("Amount: ");
            BigDecimal amt = getBigDecimalInput();
            txService.withdraw(currentAccount.getAccountId(), amt);
            currentAccount = accountService.findAccountById(currentAccount.getAccountId());
            logger.info("✅ Withdrawal successful. New balance: $" + currentAccount.getBalance());
        }
        catch (AccountNotFoundException e) {
            logger.severe("❌ Account not found: " + e.getMessage());
        } catch (InsufficientFundsException e) {
            logger.warning("⚠️ " + e.getMessage());
        } catch (Exception e) {
            logger.severe("❌ Unexpected error: " + e.getMessage());
        }
    }

    private static void transfer() {
        try {
            // Fetch other accounts
            List<Account> otherAccounts = accountService.getOtherAccounts(currentAccount.getAccountId());

            if (otherAccounts.isEmpty()) {
                logger.warning("📭 No other accounts available for transfer.");
                return;
            }

            // Display list with index
            String temp="";
            temp+="\n=== SELECT RECIPIENT ACCOUNT ===";
            for (int i = 0; i < otherAccounts.size(); i++) {
                Account acc = otherAccounts.get(i);
                temp+="\n"+String.format("%d. Account ID: %d | Owner: %s", i + 1, acc.getAccountId(), acc.getOwnerName());
            }
            temp+="\n0. Cancel Transfer";
            temp+="Choose recipient (1-" + otherAccounts.size() + "): ";
            logger.info(temp);

            int choice = getIntInput();
            if (choice == 0) {
                logger.info("✅ Transfer cancelled.");
                return;
            }

            if (choice < 1 || choice > otherAccounts.size()) {
                logger.warning("❌ Invalid selection.");
                return;
            }

            Account recipient = otherAccounts.get(choice - 1);
            logger.info("Selected: " + recipient.getOwnerName() + " (ID: " + recipient.getAccountId() + ")\nEnter amount to transfer: ");
            BigDecimal amount = getBigDecimalInput();

            // Perform transfer
            txService.transfer(currentAccount.getAccountId(), recipient.getAccountId(), amount);
            currentAccount = accountService.findAccountById(currentAccount.getAccountId());
            logger.info("✅ Transfer of $" + amount + " to " + recipient.getOwnerName() + " completed successfully.");

        } catch (AccountNotFoundException e) {
            logger.severe("❌ " + e.getMessage());
        } catch (InsufficientFundsException e) {
            logger.warning("⚠️ " + e.getMessage());
        } catch (Exception e) {
            logger.severe("❌ Transfer failed: " + e.getMessage());
        }
    }

    private static void checkAlerts() {
        alertService.checkAndAlert(currentAccount);
        logger.info(String.format("📊 Account Balance: $%.2f", currentAccount.getBalance())+"\n✅ Alert check completed.");

    }

    private static void generateReport() {
        reportGen.generateAccountSummary(currentAccount);
    }

    private static void viewAllAccounts() throws SQLException {
        List<Account> accounts = accountService.getAllAccounts();
        if (accounts.isEmpty()) {
            logger.info("📭 No accounts found.");
        } else {
            logger.info("\n=== ALL CUSTOMER ACCOUNTS ===");
            String temp="";
            for (Account acc : accounts) {
                temp+="\n"+acc.toString();
            }
            logger.info(temp);

        }
    }

    private static void viewAllTransactions() throws SQLException {
        List<Map<String, Object>> txs = accountService.getAllTransactions();
        if (txs.isEmpty()) {
            logger.info("📭 No transactions found.");
        } else {
            logger.info("\n=== ALL TRANSACTIONS (LATEST FIRST) ===");
            String temp="";
            for (Map<String, Object> tx : txs) {
                String from = tx.get("from") == null ? "SYSTEM" : tx.get("from").toString();
                String to = tx.get("to") == null ? "SYSTEM" : tx.get("to").toString();
                String line = String.format("[%s] %s | %s → %s | $%.2f | %s",
                        tx.get("timestamp"),
                        tx.get("type"),
                        from,
                        to,
                        ((BigDecimal) tx.get("amount")).doubleValue(),
                        tx.get("status")
                );
                temp+="\n"+line;
            }
            logger.info(temp);
        }
    }

    private static BigDecimal getBigDecimalInput() {
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                logger.warning("Invalid number. Try again: ");
            }
        }
    }

    private static int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                logger.warning("Invalid number. Try again: ");
            }
        }
    }
}