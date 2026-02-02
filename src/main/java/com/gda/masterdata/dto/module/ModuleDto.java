package com.gda.masterdata.dto.module;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ModuleDto {
    private String id;
    private String title;
    private String description;
    private String icon;
    private String status;
    private boolean hasAccess; // Можно ли вообще кликнуть на модуль
    private List<GroupDto> groups;
}
