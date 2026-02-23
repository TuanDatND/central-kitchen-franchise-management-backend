package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.Role;
<<<<<<< feature/Login-Authorize
import com.CocOgreen.CenFra.MS.enums.RoleName;
=======
>>>>>>> main
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
<<<<<<< feature/Login-Authorize
    Optional<Role> findByRoleName(RoleName roleName);
=======
    Optional<Role> findByRoleName(String roleName);
>>>>>>> main
}
