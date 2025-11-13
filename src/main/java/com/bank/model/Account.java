package com.bank.model;

import java.math.BigDecimal;

public class Account {
    private int accountId;
    private String ownerName;
    private String email;
    private String password;
    private BigDecimal balance;

    public Account(int accountId, String ownerName, String email, String password, BigDecimal balance) {
        this.accountId = accountId;
        this.ownerName = ownerName;
        this.email = email;
        this.password = password;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
    }

    public int getAccountId() { return accountId; }
    public String getOwnerName() { return ownerName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    @Override
    public String toString() {
        return String.format("Account[ID=%d, Owner=%s, Email=%s, Balance=$%.2f]",
                accountId, ownerName, email, balance);
    }
}