package com.gda.masterdata.entity.portal;

import com.gda.masterdata.enums.ModuleStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "portal_modules")
@Getter
@Setter
public class PortalModuleEntity {
    @Id
    private String id;
    private String title;
    private String description;
    private String icon;

    @Enumerated(EnumType.STRING)
    private ModuleStatus status;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("sortOrder ASC")
    private List<PortalModuleGroupEntity> groups = new ArrayList<>();
}
