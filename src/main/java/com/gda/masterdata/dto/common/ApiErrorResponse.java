package com.gda.masterdata.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard error response structure for the frontend.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponse {
    private int status;          // e.g., 401, 403, 400
    private String error;        // e.g., "Unauthorized"
    private String message;      // e.g., "Invalid email or password"
    private String path;         // e.g., "/api/auth/login"
    private LocalDateTime timestamp;
}