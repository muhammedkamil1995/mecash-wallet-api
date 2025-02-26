package com.mecash.wallet.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "roles")
public class Role implements Serializable {  // 🔥 Implements Serializable
    private static final long serialVersionUID = 1L;  // 🔥 Add serialVersionUID

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

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleType getName() { 
        return name;
    }

    public void setName(RoleType name) { 
        this.name = name; 
    }
}
