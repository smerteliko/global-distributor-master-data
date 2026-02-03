package com.gda.masterdata.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter that intercepts every request to check for a JWT token.
 * It validates the token and sets the authentication in the Spring Security Context.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Skip OPTIONS requests (CORS preflight)
        // These are handled by the CorsConfiguration or Nginx, we don't need to auth them.
        if (request.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Check if Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract the token (remove "Bearer " prefix)
        jwt = authHeader.substring(7);

        try {
            // 4. Extract username (email) from token
            userEmail = jwtService.extractUsername(jwt);

            // 5. If user email is found and no authentication is currently set in context
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load user details from database to ensure user exists and is active
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 6. Validate the token against the user details
                if (jwtService.isTokenValid(jwt, userDetails)) {

                    // --- CRITICAL: Extract Roles manually from the "role" claim ---
                    // Your token generator puts roles in a key named "role" (singular).
                    // Standard Spring often looks for "roles" or "authorities".
                    List<String> rolesFromToken = jwtService.extractClaim(jwt, claims -> {
                        try {
                            // Safely try to cast the claim to a List
                            return claims.get("role", List.class);
                        } catch (Exception e) {
                            return new ArrayList<>();
                        }
                    });

                    // Convert string roles to GrantedAuthority objects
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    if (rolesFromToken != null) {
                        for (Object roleObj : rolesFromToken) {
                            String role = roleObj.toString();

                            // Spring Security expects the "ROLE_" prefix for authority checks
                            if (!role.startsWith("ROLE_")) {
                                role = "ROLE_" + role;
                            }
                            authorities.add(new SimpleGrantedAuthority(role));
                        }
                    }

                    // 7. Create the Authentication object
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities // <--- Passing authorities extracted from Token
                    );

                    // Add request details (IP, Session ID, etc.)
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 8. Set the authentication in the Security Context
                    // This officially logs the user in for this request
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log authentication failure but allow the chain to continue.
            // If the endpoint requires auth, Spring will return 403 later.
            // Use a proper logger in production (e.g., Slf4j).
            System.err.println("JWT Authentication Failed: " + e.getMessage());
        }

        // 9. Continue the filter chain
        filterChain.doFilter(request, response);
    }
}