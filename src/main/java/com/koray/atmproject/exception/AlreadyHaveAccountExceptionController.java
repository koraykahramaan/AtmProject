package com.koray.atmproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AlreadyHaveAccountExceptionController {
    @ExceptionHandler(value = AlreadyHaveAccountException.class)
    public ResponseEntity<Object> exception(AlreadyHaveAccountException exception) {
        return new ResponseEntity<>("You have already an account", HttpStatus.BAD_REQUEST);
    }
}
