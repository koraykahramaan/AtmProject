package com.koray.atmproject.exception;

public class AccountNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    int accountId;

    String message;
    public AccountNotFoundException(Integer id) {
        this.accountId = id;
    }

    public AccountNotFoundException(String message) {
        this.message = message;
    }
}
