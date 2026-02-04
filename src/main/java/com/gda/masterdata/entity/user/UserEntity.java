package com.gda.masterdata.entity.user;

import com.gda.masterdata.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;

    @ManyToMany(fetch = FetchType.EAGER) // EAGER нужен, чтобы Security Context сразу получил роли при загрузке юзера
    @JoinTable(
        name = "users_roles_link",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<UserRoleEntity> roles = new HashSet<>();

    @Builder.Default
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserType userType = UserType.USER;

    @Column(name = "api_token", length = 512)
    private String apiToken;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getId()))
            .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    public void addRole(UserRoleEntity role) {
        this.roles.add(role);
    }
}
