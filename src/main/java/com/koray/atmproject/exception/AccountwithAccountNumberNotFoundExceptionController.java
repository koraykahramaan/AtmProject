package com.koray.atmproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AccountwithAccountNumberNotFoundExceptionController {

    @ExceptionHandler(AccountwithAccountNumberNotFoundException.class)
    public ResponseEntity<Object> exception(AccountwithAccountNumberNotFoundException exception) {
        return new ResponseEntity<>("Account with " + exception.accountNumber + " not found", HttpStatus.NOT_FOUND);
    }

}
