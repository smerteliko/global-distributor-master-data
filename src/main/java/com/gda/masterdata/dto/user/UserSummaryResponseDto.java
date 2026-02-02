package com.gda.masterdata.dto.user;

import lombok.Data;

@Data
public class UserSummaryResponseDto {
    protected String email;
    protected String firstName;
    protected String lastName;
    protected String token;
    protected String role;
    protected Boolean enabled;
}
