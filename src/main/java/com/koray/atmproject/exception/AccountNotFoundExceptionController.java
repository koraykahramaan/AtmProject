package com.koray.atmproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AccountNotFoundExceptionController {

    @ExceptionHandler(value = AccountNotFoundException.class)
    public ResponseEntity<Object> exception(AccountNotFoundException exception) {
        if(exception.acc_id != 0) {
            return new ResponseEntity<Object>("Account not found with id : " + exception.acc_id, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Object>(exception.message, HttpStatus.NOT_FOUND);
    }


}
