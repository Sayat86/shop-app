package com.example.shopapp.auth.service;

import com.example.shopapp.auth.dto.AuthResponse;
import com.example.shopapp.auth.dto.LoginRequest;
import com.example.shopapp.auth.dto.RegisterRequest;
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

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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
        String refreshToken = jwtService.generateRefreshToken(saved);

        return buildAuthResponse(saved, accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmailAndDeletedFalse(request.email())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return buildAuthResponse(user, accessToken, refreshToken);
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
}
