package com.cloudkitchenbackend.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String userEmail;
    private String refreshToken;
    private Instant expiresIn;

    public RefreshToken() {}

    public RefreshToken(String userEmail, String refreshToken, Instant expiresIn) {
        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public String getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Instant getExpiresIn() {
        return expiresIn;
    }
}
