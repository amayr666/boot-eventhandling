package com.amayr.demo.exception;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Globally translates exceptions to specific HTTP Statuscodes
 */
@RestControllerAdvice
public class GlobalRestExceptionTranslator extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    ResponseEntity<?> handle(DuplicateKeyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex);
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    ResponseEntity<?> handle(DocumentNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}
