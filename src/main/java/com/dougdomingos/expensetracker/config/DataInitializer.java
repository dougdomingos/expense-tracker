package com.dougdomingos.expensetracker.config;

import java.util.Optional;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.dougdomingos.expensetracker.entities.user.Role;
import com.dougdomingos.expensetracker.entities.user.Role.TypeRole;
import com.dougdomingos.expensetracker.entities.user.User;
import com.dougdomingos.expensetracker.repositories.RolesRepository;
import com.dougdomingos.expensetracker.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolesRepository roles;

    private final UserRepository users;

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initRolesInDB();
        createAdminUser();
    }

    /**
     * Insert the user roles into the database if not present.
     */
    private void initRolesInDB() {
        if (!roles.findByRoleName(TypeRole.USER).isPresent()) {
            Role userRole = Role.builder()
                    .roleName(Role.TypeRole.USER)
                    .build();
            roles.save(userRole);
        }

        if (!roles.findByRoleName(TypeRole.ADMIN).isPresent()) {
            Role adminRole = Role.builder()
                    .roleName(Role.TypeRole.ADMIN)
                    .build();
            roles.save(adminRole);
        }
    }

    /**
     * Create the Admin user.
     */
    private void createAdminUser() {
        Optional<Role> adminRole = roles.findByRoleName(Role.TypeRole.ADMIN);
        Optional<User> userAdmin = users.findByUsername("admin");

        if (!adminRole.isPresent()) {
            throw new RuntimeException("Admin role not created");
        }

        userAdmin.ifPresentOrElse(
                (user) -> {
                    System.out.println("Admin is already created!");
                },
                () -> {
                    User admin = User.builder()
                            .username("admin")
                            .password(passwordEncoder.encode("admin"))
                            .roles(Set.of(adminRole.get()))
                            .build();

                    users.save(admin);
                });
    }
}
