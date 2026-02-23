package com.CocOgreen.CenFra.MS.config;

import com.CocOgreen.CenFra.MS.entity.Role;
import com.CocOgreen.CenFra.MS.entity.User;
import com.CocOgreen.CenFra.MS.enums.RoleName;
import com.CocOgreen.CenFra.MS.repository.RoleRepository;
import com.CocOgreen.CenFra.MS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run( String... args) {

        for (RoleName roleName : RoleName.values()) {
            roleRepository.findByRoleName(roleName)
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setRoleName(roleName);
                        return roleRepository.save(role);
                    });
        }

        createUser("admin", RoleName.ADMIN);
        createUser("store", RoleName.STORE_MANAGER);
        createUser("coordinator", RoleName.COORDINATOR);
        createUser("chef", RoleName.CHEF);

        System.out.println("✅ Data seeded");
    }

    private void createUser(String username, RoleName roleName) {

        if (userRepository.findByUserName(username).isPresent()) return;

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow();

        User user = new User();
        user.setUserName(username);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setIsActive(true);
        user.setRole(role);

        userRepository.save(user);
    }
}

