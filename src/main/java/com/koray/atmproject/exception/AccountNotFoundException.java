package com.koray.atmproject.exception;

public class AccountNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    int acc_id;

    String message;
    public AccountNotFoundException(Integer id) {
        this.acc_id = id;
    }

    public AccountNotFoundException(String message) {
        this.message = message;
    }
}
