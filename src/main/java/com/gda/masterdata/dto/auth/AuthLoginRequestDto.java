package com.gda.masterdata.dto.auth;

import lombok.Data;

@Data
public class AuthLoginRequestDto {
    private String email;
    private String password;
}
