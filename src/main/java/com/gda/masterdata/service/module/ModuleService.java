package com.gda.masterdata.service.module;

import com.gda.masterdata.dto.module.GroupDto;
import com.gda.masterdata.dto.module.ModuleDto;
import com.gda.masterdata.entity.portal.PortalModuleEntity;
import com.gda.masterdata.entity.portal.PortalModuleGroupEntity;
import com.gda.masterdata.entity.user.UserRole;
import com.gda.masterdata.repository.PortalModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final PortalModuleRepository moduleRepository;

    public List<ModuleDto> getModulesForCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Set<String> userRoles = (auth != null)
            ? auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet())
            : Collections.emptySet();

        // Достаем все, кроме DISABLED (скрытых полностью)
        // Если статус MAINTENANCE - мы все равно его покажем, но пометим флагом
        return moduleRepository.findAllByOrderBySortOrderAsc().stream()
            .filter(m -> !"DISABLED".equals(m.getStatus().name()))
            .map(module -> mapToModuleDto(module, userRoles))
            .collect(Collectors.toList());
    }

    private ModuleDto mapToModuleDto(PortalModuleEntity module, Set<String> userRoles) {
        // Маппим группы
        List<GroupDto> groupDtos = module.getGroups().stream()
            .map(group -> mapToGroupDto(group, userRoles))
            .collect(Collectors.toList());

        // Логика доступа к МОДУЛЮ:
        // Если модуль ACTIVE и у пользователя есть доступ ХОТЯ БЫ к одной группе -> hasAccess = true.
        // (Или если групп вообще нет, но модуль активен — считаем публичным, как LSEG в примере)
        boolean hasModuleAccess = false;

        if (userRoles.contains("ROLE_ADMIN")) {
            hasModuleAccess = true;
        } else {
            // ОБЫЧНЫЙ ЮЗЕР:
            // Доступ есть ТОЛЬКО если есть хотя бы одна доступная группа.
            // Если групп нет (empty) -> anyMatch вернет false -> Доступ закрыт.
            // Если группы есть, но во всех hasAccess=false -> Доступ закрыт.
            hasModuleAccess = groupDtos.stream().anyMatch(GroupDto::isHasAccess);
        }

        if ("MAINTENANCE".equals(module.getStatus().name()) && !userRoles.contains("ROLE_ADMIN")) {
            hasModuleAccess = false;
        }

        return ModuleDto.builder()
            .id(module.getId())
            .title(module.getTitle())
            .description(module.getDescription())
            .icon(module.getIcon())
            .status(module.getStatus().name())
            .hasAccess(hasModuleAccess)
            .groups(groupDtos)
            .build();
    }

    private GroupDto mapToGroupDto(PortalModuleGroupEntity group, Set<String> userRoles) {
        boolean hasGroupAccess = false;

        Set<String> requiredRoleIds = group.getRequiredRoles().stream()
            .map(UserRole::getId)
            .collect(Collectors.toSet());


        if (userRoles.contains("ROLE_ADMIN")) {
            hasGroupAccess = true;
        } else if (requiredRoleIds.isEmpty()) {
            hasGroupAccess = true;
        } else {
            hasGroupAccess = !Collections.disjoint(requiredRoleIds, userRoles);
        }

        return GroupDto.builder()
            .id(group.getId())
            .name(group.getName())
            .url(group.getUrl())
            .requiredRoles(group.getRequiredRoles())
            .hasAccess(hasGroupAccess)
            .build();
    }
}