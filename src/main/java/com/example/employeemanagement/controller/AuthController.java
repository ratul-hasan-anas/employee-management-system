
package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.LoginRequest;
import com.example.employeemanagement.dto.RefreshTokenRequest;
import com.example.employeemanagement.entity.AppUser;
import com.example.employeemanagement.entity.RefreshToken;
import com.example.employeemanagement.repository.AppUserRepository;
import com.example.employeemanagement.service.JwtService;
import com.example.employeemanagement.service.RefreshTokenService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    // Constructor Injection
    public AuthController(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {

        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    // ================= REGISTRATION =================

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody AppUser appUser) {

        // Check username already exists
        if (appUserRepository.findByUsername(
                appUser.getUsername()).isPresent()) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Username already exists");
        }

        // Encrypt password using BCrypt
        appUser.setPassword(
                passwordEncoder.encode(appUser.getPassword())
        );

        // Every new registered user gets USER role
        appUser.setRole("USER");

        // Save user into database
        appUserRepository.save(appUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    // ================= LOGIN =================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        try {

            // Authenticate username and password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Find authenticated user
            AppUser user = appUserRepository
                    .findByUsername(request.getUsername())
                    .orElseThrow();

            // Generate Access Token
            String accessToken = jwtService.generateToken(
                    user.getUsername()
            );

            // Generate and save Refresh Token
            RefreshToken refreshToken =
                    refreshTokenService.createRefreshToken(user);

            // Return both tokens to client
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Login successful",
                            "token", accessToken,
                            "tokenType", "Bearer",
                            "refreshToken", refreshToken.getToken()
                    )
            );

        } catch (AuthenticationException exception) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }

    // ================= REFRESH TOKEN =================

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        try {

            // Find Refresh Token in database
            RefreshToken refreshToken =
                    refreshTokenService.findByToken(
                            request.getRefreshToken()
                    );

            // Verify token expiration
            refreshTokenService.verifyExpiration(refreshToken);

            // Get associated user
            AppUser user = refreshToken.getUser();

            // Generate new Access Token
            String newAccessToken =
                    jwtService.generateToken(user.getUsername());

            // Generate new Refresh Token (Rotation)
            RefreshToken newRefreshToken =
                    refreshTokenService.createRefreshToken(user);

            // Return new tokens
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Token refreshed successfully",
                            "token", newAccessToken,
                            "tokenType", "Bearer",
                            "refreshToken", newRefreshToken.getToken()
                    )
            );

        } catch (IllegalArgumentException exception) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", exception.getMessage()
                            )
                    );
        }
    }

    // ================= LOGOUT =================

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @Valid @RequestBody RefreshTokenRequest request) {

        try {

            // Revoke the user's Refresh Token
            refreshTokenService.revokeRefreshToken(
                    request.getRefreshToken()
            );

            // Return successful logout response
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Logout successful"
                    )
            );

        } catch (IllegalArgumentException exception) {

            // Token not found or invalid
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", "Invalid refresh token"
                            )
                    );
        }
    }
}
