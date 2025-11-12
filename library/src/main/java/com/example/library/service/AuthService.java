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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
//        var token = new UsernamePasswordAuthenticationToken(request.username(), request.password());
//        authManager.authenticate(token);
//        String jwt = jwtService.generate(request.username());
//
//        return new AuthResponse(jwt);
        var token = new UsernamePasswordAuthenticationToken(request.username(), request.password());

        // 2. BẮT LẤY KẾT QUẢ! (Đây là dòng quan trọng nhất)
        // Dòng này trả về đối tượng chứa user đã được xác thực thành công
        Authentication authentication = authManager.authenticate(token);

        // 3. Lấy UserDetails từ kết quả (đây là user do LibraryUserDetailsService tạo ra)
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 4. "Moi" role ra
        String role = userDetails.getAuthorities().stream()
                .findFirst() // Lấy role đầu tiên (giả sử mỗi user 1 role)
                .map(grantedAuthority -> grantedAuthority.getAuthority()) // Lấy tên (vd: "ROLE_ADMIN")
                .orElse("ROLE_USER"); // Giá trị dự phòng (nên có)

        // 5. Tạo JWT
        // (Tốt hơn là dùng username từ 'userDetails' đã được xác thực
        // thay vì 'request.username()' chưa chắc chắn)
        String jwt = jwtService.generate(userDetails.getUsername());

        // 6. Trả về AuthResponse mới với 2 tham số
        return new AuthResponse(jwt, role);
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
