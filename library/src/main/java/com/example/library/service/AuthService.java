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
import java.util.Optional;

/**
 * Service xử lý đăng ký và đăng nhập người dùng.
 */
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UserAccountRepository userRepo;
    private final LibraryCardRepository libraryCardRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    /**
     * Đăng ký người dùng mới.
     *
     * @param request Dữ liệu đăng ký
     * @param asAdmin true nếu là admin
     */
    @Override
    @Transactional
    public void register(RegisterRequest request, boolean asAdmin) {
        // Kiểm tra username trùng
        if (userRepo.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already taken");
        }

        // Kiểm tra email trùng (nếu có)
        if (request.email() != null && userRepo.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Tìm role phù hợp
        String roleName = asAdmin ? "ROLE_ADMIN" : "ROLE_READER";
        Optional<Role> roleOpt = roleRepo.findByRoleName(roleName);

        if (roleOpt.isEmpty()) {
            throw new IllegalStateException("Role not found: " + roleName);
        }

        // Tạo UserAccount
        UserAccount user = UserAccount.builder()
                .fullName(request.fullName())
                .email(request.email())
                .phoneNumber(request.phoneOrNull())
                .username(request.username())
                .passwordHash(encoder.encode(request.password()))
                .status(UserStatus.ACTIVE)
                .role(roleOpt.get())
                .build();

        user = userRepo.save(user);
        createLibraryCardForUser(user);
    }

    /**
     * Đăng nhập người dùng.
     *
     * @param request Dữ liệu đăng nhập
     * @return JWT token
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
        );
        authManager.authenticate(authToken);
        String jwt = jwtService.generate(request.username());
        return new AuthResponse(jwt);
    }

    /**
     * Tạo thẻ thư viện tự động cho người dùng mới.
     *
     * @param user Người dùng
     */
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

    /**
     * Tạo số thẻ duy nhất.
     *
     * @return Số thẻ dạng CARDxxxxxx
     */
    private String generateUniqueCardNumber() {
        String cardNumber;
        do {
            cardNumber = "CARD" + generateRandomDigits(6);
        } while (libraryCardRepo.existsByCardNumber(cardNumber));
        return cardNumber;
    }

    /**
     * Tạo chuỗi số ngẫu nhiên.
     *
     * @param length Độ dài chuỗi
     * @return Chuỗi số
     */
    private String generateRandomDigits(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int digit = java.util.concurrent.ThreadLocalRandom.current().nextInt(10);
            sb.append(digit);
        }
        return sb.toString();
    }
}