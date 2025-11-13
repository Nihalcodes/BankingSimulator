package com.bank.exception;

public class DuplicateAccountException extends Exception {
    public DuplicateAccountException(String message) {
        super(message);
    }
}
