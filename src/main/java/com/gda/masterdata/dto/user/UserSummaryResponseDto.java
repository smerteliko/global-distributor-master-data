package com.gda.masterdata.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gda.masterdata.entity.user.UserRoleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSummaryResponseDto {
    protected String email;
    protected String firstName;
    protected String lastName;
    protected String token;
    @JsonProperty("role")
    private Set<UserRoleEntity> roles;
    protected Boolean enabled;
}
