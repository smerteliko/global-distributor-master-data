package com.gda.masterdata.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "permissions") // Просто permissions, без приставки portal
@Getter @Setter
public class Permission {
    @Id
    private String id;
    private String description;
}
