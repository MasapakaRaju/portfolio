package com.portfolio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "admin_accounts", uniqueConstraints = @UniqueConstraint(columnNames = "username_hash"))
@Getter
@Setter
@NoArgsConstructor
public class AdminAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username_hash", nullable = false, length = 64)
    private String usernameHash;

    @Column(nullable = false)
    private boolean active = true;
}
