package com.gda.masterdata.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gda.masterdata.dto.common.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Handles authentication errors that happen in the Filter Chain (e.g., missing or invalid JWT).
 * Returns a JSON response instead of the default 403 HTML page.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
        throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
            .status(HttpServletResponse.SC_UNAUTHORIZED)
            .error("Unauthorized")
            .message("Access denied. Missing or invalid authentication token.")
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();

        // Write JSON directly to the response stream
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}