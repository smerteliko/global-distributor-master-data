package com.gda.masterdata.dto.module;

import com.gda.masterdata.entity.user.UserRoleEntity;
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
    private Set<UserRoleEntity> requiredRoles; // Для дебага или UI
}
