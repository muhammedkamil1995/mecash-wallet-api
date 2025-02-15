package com.mecash.wallet.repository;

import com.mecash.wallet.model.Role;
import com.mecash.wallet.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}
