package com.koray.atmproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AccountNotFoundExceptionController {

    @ExceptionHandler(value = AccountNotFoundException.class)
    public ResponseEntity<Object> exception(AccountNotFoundException exception) {
        if(exception.accountId != 0) {
            return new ResponseEntity<>("Account not found with id : " + exception.accountId, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(exception.message, HttpStatus.NOT_FOUND);
    }


}
