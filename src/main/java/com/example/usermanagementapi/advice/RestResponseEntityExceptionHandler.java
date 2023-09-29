package com.example.usermanagementapi.advice;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(
            RuntimeException ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("errorMessage", ex.getMessage());
        map.put("httpStatus", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgument(
            RuntimeException ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("errorMessage", ex.getMessage());
        map.put("httpStatus", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityViolation() {
        Map<String, Object> map = new HashMap<>();
        map.put("errorMessage", "A user with this email already exists.");
        map.put("httpStatus", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }
}
