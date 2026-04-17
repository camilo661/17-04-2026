package com.aiproxy.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimit(RateLimitExceededException ex) {
        Map<String, Object> body = buildErrorBody(429, "RATE_LIMIT_EXCEEDED", ex.getMessage());
        body.put("retryAfterSeconds", ex.getRetryAfterSeconds());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Retry-After", String.valueOf(ex.getRetryAfterSeconds()));

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).headers(headers).body(body);
    }

    @ExceptionHandler(QuotaExhaustedException.class)
    public ResponseEntity<Map<String, Object>> handleQuotaExhausted(QuotaExhaustedException ex) {
        Map<String, Object> body = buildErrorBody(402, "QUOTA_EXHAUSTED", ex.getMessage());
        body.put("resetDate", ex.getResetDate() != null ? ex.getResetDate().toString() : null);
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst().orElse("Validation error");
        Map<String, Object> body = buildErrorBody(400, "VALIDATION_ERROR", msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        Map<String, Object> body = buildErrorBody(500, "INTERNAL_ERROR", "An unexpected error occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private Map<String, Object> buildErrorBody(int status, String code, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("code", code);
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now().toString());
        return body;
    }
}
