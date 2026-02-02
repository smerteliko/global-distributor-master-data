package com.gda.masterdata.dto.module;

import com.gda.masterdata.entity.user.UserRole;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class GroupDto {
    private String id;
    private String name;
    private String url;
    private boolean hasAccess; // Можно ли перейти в эту подгруппу
    private Set<UserRole> requiredRoles; // Для дебага или UI
}
