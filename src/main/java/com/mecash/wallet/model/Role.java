package com.mecash.wallet.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleType name;  // RoleType ENUM

    // Constructors
    public Role() {}

    public Role(RoleType name) {
        this.name = name;
    }

    // Getter for Role Name
    public RoleType getName() { 
        return name;
    }

    public void setName(RoleType name) { this.name = name; }
}
