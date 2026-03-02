package com.example.shopapp.auth.service;

import com.example.shopapp.auth.dto.AuthResponse;
import com.example.shopapp.auth.dto.LoginRequest;
import com.example.shopapp.auth.dto.RegisterRequest;
import com.example.shopapp.auth.entity.RefreshToken;
import com.example.shopapp.auth.repository.RefreshTokenRepository;
import com.example.shopapp.exception.BadRequestException;
import com.example.shopapp.security.JwtService;
import com.example.shopapp.user.entity.Role;
import com.example.shopapp.user.entity.User;
import com.example.shopapp.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmailAndDeletedFalse(request.email())) {
            throw new BadRequestException("Email already exists");
        }

        if (userRepository.existsByUsernameAndDeletedFalse(request.username())) {
            throw new BadRequestException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
        user.setDeleted(false);

        User saved = userRepository.save(user);

        String accessToken = jwtService.generateToken(saved);
        refreshTokenRepository.deleteByUser(saved);
        RefreshToken refreshToken = createRefreshToken(saved);

        return buildAuthResponse(saved, accessToken, refreshToken.getToken());
    }

    public AuthResponse login(LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (org.springframework.security.core.AuthenticationException ex) {
            throw new BadRequestException("Invalid credentials");
        }

        User user = getActiveUserByEmail(request.email());

        if (!user.isEnabled()) {
            throw new BadRequestException("User account is disabled");
        }

        refreshTokenRepository.deleteByUser(user);

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        return buildAuthResponse(user, accessToken, refreshToken.getToken());
    }

    public AuthResponse refresh(String requestToken) {

        RefreshToken oldToken = refreshTokenRepository.findByToken(requestToken)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        // 🚨 reuse detection
        if (oldToken.isRevoked()) {

            // optional: invalidate all sessions of this user
            refreshTokenRepository.deleteByUser(oldToken.getUser());

            throw new BadRequestException("Refresh token reuse detected");
        }

        if (oldToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Refresh token expired");
        }

        User user = oldToken.getUser();

        if (!user.isEnabled() || user.isDeleted()) {
            throw new BadRequestException("User is not active");
        }

        // 🔁 ROTATION
        oldToken.setRevoked(true);

        RefreshToken newRefreshToken = createRefreshToken(user);
        String newAccessToken = jwtService.generateToken(user);

        return buildAuthResponse(
                user,
                newAccessToken,
                newRefreshToken.getToken()
        );
    }

    public void logout(String refreshTokenValue) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        refreshToken.setRevoked(true);
    }

    // ---------------- PRIVATE HELPERS ----------------

    private RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    private AuthResponse buildAuthResponse(User user,
                                           String accessToken,
                                           String refreshToken) {

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                accessToken,
                refreshToken
        );
    }

    private User getActiveUserByEmail(String email) {
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
    }
}
