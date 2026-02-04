package com.gda.masterdata.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_roles")
@Getter @Setter
public class UserRoleEntity {

    @Id
    private String id; // ROLE_ADMIN

    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<PermissionEntity> permissions = new HashSet<>();
}