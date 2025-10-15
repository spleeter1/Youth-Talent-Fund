package com.graduation.youthtalentfund.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, nullable = false, unique = true)
    private String name; // "ROLE_USER", "ROLE_ADMIN, "ROLE_STAFF"

    @OneToMany(mappedBy = "role")
    private Set<UserRole> userRoles = new HashSet<>();
}