package com.dougdomingos.expensetracker.entities.user;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a role assigned an user. It is used to control the access to the
 * application.
 */
@Data
@Entity
@Builder
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeRole roleName;

    @Builder.Default
    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();

    public enum TypeRole {
        ADMIN,
        USER
    }
}
