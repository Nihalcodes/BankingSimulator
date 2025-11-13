package com.bank.service;

import com.bank.exception.InsufficientFundsException;
import com.bank.model.Account;
import com.bank.util.TransactionLogger;

import java.math.BigDecimal;

public class TransactionService {
    private final AccountService accountService;
    private final TransactionLogger logger;

    public TransactionService(AccountService accountService, TransactionLogger logger) {
        this.accountService = accountService;
        this.logger = logger;
    }

    public void deposit(int accountId, BigDecimal amount) throws Exception {
        validateAmount(amount);
        Account acc = accountService.findAccountById(accountId);
        if (acc == null) throw new Exception("Account not found");

        acc.setBalance(acc.getBalance().add(amount));
        accountService.updateBalance(accountId, acc.getBalance());
        logger.logTransaction(accountId, null, amount, "DEPOSIT", "SUCCESS");
    }

    public void withdraw(int accountId, BigDecimal amount) throws Exception {
        validateAmount(amount);
        Account acc = accountService.findAccountById(accountId);
        if (acc == null) throw new Exception("Account not found");
        if (acc.getBalance().compareTo(amount) < 0) {
            logger.logTransaction(accountId, null, amount, "WITHDRAWAL", "FAILED");
            throw new InsufficientFundsException("Insufficient funds for withdrawal");
        }

        acc.setBalance(acc.getBalance().subtract(amount));
        accountService.updateBalance(accountId, acc.getBalance());
        logger.logTransaction(accountId, null, amount, "WITHDRAWAL", "SUCCESS");
    }

    public void transfer(int fromId, int toId, BigDecimal amount) throws Exception {
        validateAmount(amount);
        Account from = accountService.findAccountById(fromId);
        Account to = accountService.findAccountById(toId);
        if (from == null || to == null) throw new Exception("One or both accounts not found");
        if (from.getBalance().compareTo(amount) < 0) {
            logger.logTransaction(fromId, toId, amount, "TRANSFER", "FAILED");
            throw new InsufficientFundsException("Insufficient funds for transfer");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        accountService.updateBalance(fromId, from.getBalance());
        accountService.updateBalance(toId, to.getBalance());
        logger.logTransaction(fromId, toId, amount, "TRANSFER", "SUCCESS");
    }

    private void validateAmount(BigDecimal amount) throws Exception {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Transaction amount must be positive");
        }
    }
}