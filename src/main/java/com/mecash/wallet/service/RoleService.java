package com.mecash.wallet.service;

import com.mecash.wallet.model.Role;
import com.mecash.wallet.model.RoleType;
import com.mecash.wallet.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    // Constructor injection (preferred)
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Optional<Role> getRoleByName(RoleType roleType) {
        return roleRepository.findByName(roleType);
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }
}
