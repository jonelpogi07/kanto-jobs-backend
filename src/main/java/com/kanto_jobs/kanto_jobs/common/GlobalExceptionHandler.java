package com.kanto_jobs.kanto_jobs.common;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,Object>> handle(ResponseStatusException ex, HttpServletRequest req) {
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
            "timestamp", LocalDateTime.now().toString(),
            "status", ex.getStatusCode().value(),
            "error", ex.getReason(),
            "message", ex.getReason(),
            "path", req.getRequestURI()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handle(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(500).body(Map.of(
            "timestamp", LocalDateTime.now().toString(),
            "status", 500,
            "error", "Internal Server Error",
            "message", ex.getMessage(),
            "path", req.getRequestURI()));
    }
}