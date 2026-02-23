package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

<<<<<<< feature/Login-Authorize
    Optional<User> findByUserName(String userName);
=======
    Optional<User> findByUsername(String username);
>>>>>>> main
}
