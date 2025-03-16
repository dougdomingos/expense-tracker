package com.dougdomingos.expensetracker.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dougdomingos.expensetracker.entities.user.Role;
import com.dougdomingos.expensetracker.entities.user.Role.TypeRole;

public interface RolesRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(TypeRole roleName);
}
