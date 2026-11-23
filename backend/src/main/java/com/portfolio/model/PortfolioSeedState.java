package com.portfolio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "portfolio_seed_state")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSeedState {
    @Id
    @Column(length = 40)
    private String id;

    @Column(name = "initialized_at", nullable = false)
    private Instant initializedAt;
}
