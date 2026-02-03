package com.gda.masterdata.exception;

import com.gda.masterdata.dto.common.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Global handler to catch exceptions thrown from Controllers and Services.
 * Converts Java exceptions into neat JSON responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handle Login Errors (Wrong Password / User not found)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password", request);
    }

    // 2. Handle Disabled User (User exists but enabled=false)
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiErrorResponse> handleDisabledUser(DisabledException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Account is disabled. Please contact support.", request);
    }

    // 3. Handle Locked User
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiErrorResponse> handleLockedUser(LockedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Account is locked due to too many failed attempts.", request);
    }

    // 4. Handle Validation Errors (e.g. invalid email format in DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed: " + errors, request);
    }

    // 5. Handle Generic/Unexpected Errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        ex.printStackTrace(); // Keep logs for backend debugging
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        ApiErrorResponse response = ApiErrorResponse.builder()
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(message)
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();
        return new ResponseEntity<>(response, status);
    }
}