//package com.example.library.service;
//
//import com.example.library.entity.PasswordResetToken;
//import com.example.library.entity.UserAccount;
//import com.example.library.repository.PasswordResetTokenRepository;
//import com.example.library.repository.UserAccountRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class PasswordService implements IPasswordService {
//    private final UserAccountRepository userRepo;
//    private final PasswordResetTokenRepository tokenRepo;
//    private final PasswordEncoder encoder;
//
//    @Override
//    public String requestReset(String usernameOrEmail) {
//        UserAccount user = userRepo.findByUsername(usernameOrEmail)
//                .or(() -> userRepo.findAll().stream()
//                        .filter(u -> usernameOrEmail.equalsIgnoreCase(u.getEmail()))
//                        .findFirst())
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        String token = UUID.randomUUID().toString();
//        PasswordResetToken t = PasswordResetToken.builder()
//                .user(user)
//                .token(token)
//                .expiresAt(LocalDateTime.now().plusMinutes(15))
//                .used(false)
//                .build();
//        tokenRepo.save(t);
//        return token;
//    }
//
//    @Override
//    public void reset(String token, String newPassword) {
//        PasswordResetToken t = tokenRepo.findByToken(token)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
//
//        if (t.isUsed() || t.getExpiresAt().isBefore(LocalDateTime.now()))
//            throw new IllegalArgumentException("Token expired or already used");
//
//        UserAccount user = t.getUser();
//        user.setPasswordHash(encoder.encode(newPassword));
//        userRepo.save(user);
//
//        t.setUsed(true);
//        tokenRepo.save(t);
//    }
//}
