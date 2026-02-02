package com.gda.masterdata.service;

import com.gda.masterdata.dto.auth.AuthLoginRequestDto;
import com.gda.masterdata.dto.auth.AuthRegisterRequestDto;
import com.gda.masterdata.dto.auth.AuthResponseDto;
import com.gda.masterdata.dto.user.UserSummaryResponseDto;
import com.gda.masterdata.entity.UserEntity;
import com.gda.masterdata.enums.UserType;
import com.gda.masterdata.repository.UserRepository;
import com.gda.masterdata.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDto register(AuthRegisterRequestDto request) {
        var user = UserEntity.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role("ROLE_USER")
            .userType(UserType.USER)
            .enabled(true)
            .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return buildAuthResponse(user, jwtToken);
    }

    public AuthResponseDto authenticate(AuthLoginRequestDto request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        var user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        var jwtToken = jwtService.generateToken(user);
        return buildAuthResponse(user, jwtToken);
    }

    private AuthResponseDto buildAuthResponse(UserEntity user, String token) {
        var summary = new UserSummaryResponseDto();
        summary.setEmail(user.getEmail());
        summary.setFirstName(user.getFirstName());
        summary.setLastName(user.getLastName());
        summary.setRole(user.getRole());
        summary.setToken(token);
        summary.setEnabled(user.isEnabled());

        var response = new AuthResponseDto();
        response.setUser(summary);
        return response;
    }
}