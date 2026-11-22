
package com.example.employeemanagement.service;

import com.example.employeemanagement.entity.AppUser;
import com.example.employeemanagement.entity.RefreshToken;
import com.example.employeemanagement.repository.RefreshTokenRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenDuration;

    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository) {

        this.refreshTokenRepository = refreshTokenRepository;
    }

    // Revoke Refresh Token during logout
    public void revokeRefreshToken(String token) {

        // Find the refresh token from database
        RefreshToken refreshToken = findByToken(token);

        // Delete the refresh token
        refreshTokenRepository.delete(refreshToken);
    }

    // Generate and save a new Refresh Token
    public RefreshToken createRefreshToken(AppUser user) {

        // Find existing refresh token for this user
        // If found, reuse the existing database row
        // Otherwise, create a new row
        RefreshToken refreshToken = refreshTokenRepository
                .findByUser(user)
                .orElseGet(RefreshToken::new);

        // Generate secure random token
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        // Set new token
        refreshToken.setToken(token);

        // Associate token with user
        refreshToken.setUser(user);

        // Set expiration time
        refreshToken.setExpiryDate(
                Instant.now().plusMillis(refreshTokenDuration)
        );

        // Save new token or update existing token
        return refreshTokenRepository.save(refreshToken);
    }

    // Verify token expiration
    public RefreshToken verifyExpiration(RefreshToken token) {

        if (token.getExpiryDate().isBefore(Instant.now())) {

            refreshTokenRepository.delete(token);

            throw new IllegalArgumentException(
                    "Refresh token has expired. Please login again."
            );
        }

        return token;
    }

    // Find Refresh Token from database
    public RefreshToken findByToken(String token) {

        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Refresh token not found"
                        )
                );
    }
}
