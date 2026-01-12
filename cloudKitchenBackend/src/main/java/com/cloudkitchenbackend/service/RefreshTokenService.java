package com.cloudkitchenbackend.service;

import com.cloudkitchenbackend.exception.InvalidTokenException;
import com.cloudkitchenbackend.model.RefreshToken;
import com.cloudkitchenbackend.repository.RefreshTokenRepo;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private RefreshTokenRepo refreshTokenRepo;
    private long refreshTokenExpirationTime = 7L;

    public RefreshTokenService(RefreshTokenRepo refreshTokenRepo) {
        this.refreshTokenRepo = refreshTokenRepo;
    }

    public RefreshToken generateRefreshToken(String email) {
        Optional<RefreshToken> existingToken = refreshTokenRepo.findByUserEmail(email);
        if(existingToken.isPresent()){
            refreshTokenRepo.delete(existingToken.get());
        }

        String refreshToken = UUID.randomUUID().toString().replace("-","");
        Instant expiresIn = Instant.now().plus(refreshTokenExpirationTime, ChronoUnit.DAYS);

        RefreshToken newToken = new RefreshToken(
                email,
                refreshToken,
                expiresIn
        );

        return refreshTokenRepo.save(newToken);
    }

    public RefreshToken validateRefreshToken(String refreshToken) {
        Optional<RefreshToken> storedToken = refreshTokenRepo.findByRefreshToken(refreshToken);
        if(storedToken.isEmpty()) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        if(storedToken.get().getExpiresIn().isBefore(Instant.now())) {
            refreshTokenRepo.delete(storedToken.get());
            throw new InvalidTokenException("Refresh token expired");
        }

        return storedToken.get();
    }

    public void deleteToken(String refreshToken) {
        Optional<RefreshToken> token = refreshTokenRepo.findByRefreshToken(refreshToken);
        if(token.isPresent()) {
            refreshTokenRepo.delete(token.get());
        }else {
            throw new InvalidTokenException("Token not found");
        }
    }
 }
