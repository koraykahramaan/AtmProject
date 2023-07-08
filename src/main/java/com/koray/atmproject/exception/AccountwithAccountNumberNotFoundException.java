package com.koray.atmproject.exception;

public class AccountwithAccountNumberNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    String accountNumber;

    public AccountwithAccountNumberNotFoundException(String accountNumber){
        this.accountNumber = accountNumber;
    }
}
