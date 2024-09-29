package com.dougdomingos.expensetracker.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.dougdomingos.expensetracker.entities.user.Role;
import com.dougdomingos.expensetracker.entities.user.Role.TypeRole;
import com.dougdomingos.expensetracker.repositories.RolesRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RolesRepository roles;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initRolesInDB();
    }

    /**
     * Insert the user roles into the database if not present.
     */
    private void initRolesInDB() {
        if (!roles.findByRoleName(TypeRole.ROLE_USER).isPresent()) {
            Role userRole = Role.builder()
                    .roleName(Role.TypeRole.ROLE_USER)
                    .build();
            roles.save(userRole);
        }

        if (!roles.findByRoleName(TypeRole.ROLE_ADMIN).isPresent()) {
            Role adminRole = Role.builder()
                    .roleName(Role.TypeRole.ROLE_ADMIN)
                    .build();
            roles.save(adminRole);
        }
    }
}
