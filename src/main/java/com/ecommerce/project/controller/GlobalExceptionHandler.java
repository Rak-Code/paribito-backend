package com.ecommerce.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntime(RuntimeException ex) {
        ApiError e = new ApiError(400, ex.getMessage());
        return ResponseEntity.badRequest().body(e);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleNotFound(IllegalArgumentException ex) {
        ApiError e = new ApiError(404, ex.getMessage());
        return ResponseEntity.status(404).body(e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception ex) {
        ApiError e = new ApiError(500, "Internal Server Error");
        return ResponseEntity.status(500).body(e);
    }

    record ApiError(int status, String message) {}
}
