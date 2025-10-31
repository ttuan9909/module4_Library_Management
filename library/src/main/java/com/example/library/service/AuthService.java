package com.example.library.service;

import com.example.library.dto.request.LoginRequest;
import com.example.library.dto.request.RegisterRequest;
import com.example.library.dto.response.AuthResponse;
import com.example.library.entity.Role;
import com.example.library.entity.UserAccount;
import com.example.library.entity.enums.UserStatus;
import com.example.library.repository.RoleRepository;
import com.example.library.repository.UserAccountRepository;
import com.example.library.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final UserAccountRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public void register(RegisterRequest request, boolean asAdmin) {
        if (userRepo.existsByUsername(request.username()))
            throw new IllegalArgumentException("Username already taken");

        if (request.email() != null && userRepo.existsByEmail(request.email()))
            throw new IllegalArgumentException("Email already registered");

        Role role = asAdmin
                ? roleRepo.findByRoleName("ROLE_ADMIN")
                : roleRepo.findByRoleName("ROLE_READER");

        UserAccount user = UserAccount.builder()
                .fullName(request.fullName())
                .email(request.email())
                .phoneNumber(request.phoneOrNull())
                .username(request.username())
                .passwordHash(encoder.encode(request.password()))
                .status(UserStatus.ACTIVE)
                .role(role)
                .build();
        userRepo.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        var token = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        authManager.authenticate(token);
        String jwt = jwtService.generate(request.username());
        return new AuthResponse(jwt);
    }
}
