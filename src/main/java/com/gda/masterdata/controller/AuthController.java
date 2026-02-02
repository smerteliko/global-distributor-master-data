package com.gda.masterdata.controller;

import com.gda.masterdata.dto.auth.AuthLoginRequestDto;
import com.gda.masterdata.dto.auth.AuthRegisterRequestDto;
import com.gda.masterdata.dto.auth.AuthResponseDto;
import com.gda.masterdata.service.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/secure/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody AuthRegisterRequestDto request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthLoginRequestDto request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
