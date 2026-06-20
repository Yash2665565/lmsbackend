package com.school.identity.service;

import com.school.config.JwtUtil;
import com.school.identity.dto.LoginRequest;
import com.school.identity.dto.LoginResponse;
import com.school.identity.entity.User;
import com.school.identity.repository.UserRepository;
import com.school.identity.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserRoleRepository userRoleRepository;

    public LoginResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        User user = userRepository.findByEmail(req.getEmail()).orElseThrow();

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, Object> extraClaims = Map.of(
                "roles", roles,
                "userId", user.getId()
        );

        String token = jwtUtil.generateToken(user, extraClaims);

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getFullName())
                .roles(roles)
                .build();
    }
}
