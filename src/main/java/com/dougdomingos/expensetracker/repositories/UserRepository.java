package com.dougdomingos.expensetracker.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dougdomingos.expensetracker.entities.user.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    User findByUserId(UUID userId);
}
