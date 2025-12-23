package com.gda.masterdata.dto.auth;

import lombok.Data;

@Data
public class AuthRegisterRequestDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
