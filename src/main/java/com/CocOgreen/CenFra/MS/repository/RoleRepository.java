package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);
}
