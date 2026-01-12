package com.cloudkitchenbackend.repository;

import com.cloudkitchenbackend.model.RefreshToken;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken,String> {

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.userEmail = :email")
    void deleteByUserEmail(@Param("email") String email);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    void deleteByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByUserEmail(String email);
}
