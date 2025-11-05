package com.example.library.service;

import com.example.library.dto.request.LoginRequest;
import com.example.library.dto.request.RegisterRequest;
import com.example.library.dto.response.AuthResponse;
import com.example.library.entity.LibraryCard;
import com.example.library.entity.Role;
import com.example.library.entity.UserAccount;
import com.example.library.entity.enums.LibraryCardStatus;
import com.example.library.entity.enums.UserStatus;
import com.example.library.repository.LibraryCardRepository;
import com.example.library.repository.RoleRepository;
import com.example.library.repository.UserAccountRepository;
import com.example.library.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final UserAccountRepository userRepo;
    private final LibraryCardRepository libraryCardRepo;
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

        if (role == null) {
            throw new IllegalStateException("Role not found");
        }

        UserAccount user = UserAccount.builder()
                .fullName(request.fullName())
                .email(request.email())
                .phoneNumber(request.phoneOrNull())
                .username(request.username())
                .passwordHash(encoder.encode(request.password()))
                .status(UserStatus.ACTIVE)
                .role(role)
                .build();
        user = userRepo.save(user);
        createLibraryCardForUser(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        var token = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        authManager.authenticate(token);
        String jwt = jwtService.generate(request.username());
        return new AuthResponse(jwt);
    }

    private void createLibraryCardForUser(UserAccount user) {
        LibraryCard card = LibraryCard.builder()
                .user(user)
                .cardNumber(generateUniqueCardNumber())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .status(LibraryCardStatus.ACTIVE)
                .notes("Thẻ được tạo tự động khi đăng ký.")
                .build();

        libraryCardRepo.save(card);
    }

    private String generateUniqueCardNumber() {
        String cardNumber;
        do {
            cardNumber = "CARD" + generateRandomDigits(6);
        } while (libraryCardRepo.existsByCardNumber(cardNumber));
        return cardNumber;
    }

    private String generateRandomDigits(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int d = java.util.concurrent.ThreadLocalRandom.current().nextInt(10);
            sb.append(d);
        }
        return sb.toString();
    }
}
