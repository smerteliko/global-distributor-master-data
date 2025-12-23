package com.gda.masterdata.dto.auth;

import com.gda.masterdata.dto.user.UserSummaryResponseDto;
import lombok.Data;

@Data
public class AuthResponseDto {
    private String token;
    private UserSummaryResponseDto user;
}
