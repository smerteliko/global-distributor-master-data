package com.gda.masterdata.entity.portal;

import com.gda.masterdata.entity.user.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "portal_module_groups")
@Getter
@Setter
public class PortalModuleGroupEntity {
    @Id
    private String id;
    private String name;
    private String url;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private PortalModuleEntity module;

    @ManyToMany(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "portal_group_required_roles",
        joinColumns = @JoinColumn(name = "group_id")
    )
    @Column(name = "role_name")
    private Set<UserRole> requiredRoles = new HashSet<>();
}
