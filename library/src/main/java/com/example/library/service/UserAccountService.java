package com.example.library.service;

import com.example.library.dto.request.ChangePasswordRequest;
import com.example.library.dto.request.LibraryCardDTO;
import com.example.library.dto.request.ProfileUpdateRequest;
import com.example.library.dto.request.UserAccountDTO;
import com.example.library.entity.LibraryCard;
import com.example.library.entity.Role;
import com.example.library.entity.UserAccount;
import com.example.library.entity.enums.LibraryCardStatus;
import com.example.library.entity.enums.UserStatus;
import com.example.library.repository.LibraryCardRepository;
import com.example.library.repository.RoleRepository;
import com.example.library.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ UserAccount và LibraryCard.
 */
@Service
@RequiredArgsConstructor
public class UserAccountService implements IUserAccountService {

    private final UserAccountRepository userRepo;
    private final RoleRepository roleRepo;
    private final LibraryCardRepository cardRepo;
    private final PasswordEncoder encoder;
    private final Random random = new Random();

    // ========================== DTO CONVERTERS ==========================

    private LibraryCardDTO toLibraryCardDTO(LibraryCard card) {
        if (card == null) return null;
        return LibraryCardDTO.builder()
                .cardNumber(card.getCardNumber())
                .startDate(card.getStartDate())
                .endDate(card.getEndDate())
                .status(card.getStatus().name())
                .notes(card.getNotes())
                .build();
    }

    private LibraryCard toLibraryCardEntity(LibraryCardDTO dto) {
        if (dto == null) return null;
        return LibraryCard.builder()
                .cardNumber(dto.getCardNumber())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .notes(dto.getNotes())
                .status(Optional.ofNullable(dto.getStatus())
                        .map(s -> LibraryCardStatus.valueOf(s.toUpperCase()))
                        .orElse(LibraryCardStatus.ACTIVE))
                .build();
    }

    private UserAccountDTO toDTO(UserAccount entity) {
        Optional<LibraryCard> cardOptional = cardRepo.findByUser_UserId(entity.getUserId());

        return UserAccountDTO.builder()
                .userId(entity.getUserId())
                .fullName(entity.getFullName())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .dateOfBirth(entity.getDateOfBirth())
                .avatarUrl(entity.getAvatarUrl())
                .roleName(entity.getRole() != null && entity.getRole().getRoleName() != null
                        ? entity.getRole().getRoleName() : "UNKNOWN_ROLE")
                .status(entity.getStatus() != null ? entity.getStatus().name() : "UNKNOWN_STATUS")
                .libraryCard(cardOptional.map(this::toLibraryCardDTO).orElse(null))
                .build();
    }

    private UserAccount toEntity(UserAccountDTO dto, Role role, UserStatus status) {
        return UserAccount.builder()
                .fullName(dto.getFullName())
                .username(dto.getUsername())
                .passwordHash(dto.getPassword() != null ? encoder.encode(dto.getPassword()) : null)
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .dateOfBirth(dto.getDateOfBirth())
                .avatarUrl(dto.getAvatarUrl())
                .role(role)
                .status(status)
                .build();
    }

    // ========================== LOGIC TẠO CARD NUMBER MỚI ==========================

    private String generateUniqueCardNumber() {
        final String PREFIX = "CARD-";
        final int MAX_ATTEMPTS = 100;
        int attempts = 0;

        while (attempts < MAX_ATTEMPTS) {
            int number = random.nextInt(1_000_000);
            String sixDigit = String.format("%06d", number);
            String cardNumber = PREFIX + sixDigit;

            if (!cardRepo.existsByCardNumber(cardNumber)) {
                return cardNumber;
            }
            attempts++;
        }

        throw new IllegalStateException("Không thể tạo số thẻ duy nhất sau " + MAX_ATTEMPTS + " lần thử.");
    }

    // ========================== CRUD ==========================

    @Override
    @Transactional(readOnly = true)
    public List<UserAccountDTO> getAllUsers() {
        return userRepo.findAll() // @EntityGraph tự động fetch role
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccountDTO getUserById(Long id) {
        UserAccount user = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + id));
        return toDTO(user);
    }

    @Override
    @Transactional
    public UserAccountDTO createUser(UserAccountDTO dto) {
        // 1. Validate trùng lặp
        if (userRepo.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }
        if (dto.getEmail() != null && userRepo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }
        if (dto.getPhoneNumber() != null && userRepo.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }

        // 2. Xử lý Role
        String roleName = Optional.ofNullable(dto.getRoleName())
                .filter(r -> !r.isBlank())
                .orElse("ROLE_READER");

        Role role = roleRepo.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Vai trò '" + roleName + "' không tồn tại"));

        // 3. Xử lý Status
        UserStatus status = Optional.ofNullable(dto.getStatus())
                .filter(s -> !s.isBlank())
                .map(s -> {
                    try {
                        return UserStatus.valueOf(s.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Trạng thái '" + s + "' không hợp lệ");
                    }
                })
                .orElse(UserStatus.ACTIVE);

        // 4. Tạo User
        UserAccount user = toEntity(dto, role, status);
        UserAccount savedUser = userRepo.save(user);

        // 5. Tạo LibraryCard (bắt buộc)
        if (dto.getLibraryCard() == null) {
            throw new IllegalArgumentException("Thông tin thẻ thư viện không được để trống");
        }

        LibraryCard card = toLibraryCardEntity(dto.getLibraryCard());
        card.setCardNumber(generateUniqueCardNumber());
        card.setUser(savedUser);

        if (card.getStartDate() == null) {
            card.setStartDate(LocalDate.now());
        }
        if (card.getEndDate() == null) {
            card.setEndDate(card.getStartDate().plusYears(1));
        }

        cardRepo.save(card);

        return toDTO(savedUser);
    }

    @Override
    @Transactional
    public UserAccountDTO updateUser(Long id, UserAccountDTO dto) {
        UserAccount user = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + id));

        // Cập nhật thông tin cơ bản
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAvatarUrl(dto.getAvatarUrl());

        // Cập nhật Role
        if (dto.getRoleName() != null && !dto.getRoleName().isBlank() &&
            (user.getRole() == null || !dto.getRoleName().equals(user.getRole().getRoleName()))) {
            Role newRole = roleRepo.findByRoleName(dto.getRoleName())
                    .orElseThrow(() -> new IllegalArgumentException("Vai trò '" + dto.getRoleName() + "' không tồn tại"));
            user.setRole(newRole);
        }

        // Cập nhật Status
        if (dto.getStatus() != null && !dto.getStatus().isBlank() &&
            (user.getStatus() == null || !user.getStatus().name().equalsIgnoreCase(dto.getStatus()))) {
            try {
                user.setStatus(UserStatus.valueOf(dto.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Trạng thái '" + dto.getStatus() + "' không hợp lệ");
            }
        }

        // Cập nhật LibraryCard
        if (dto.getLibraryCard() != null) {
            LibraryCard card = cardRepo.findByUser_UserId(id)
                    .orElseGet(() -> {
                        LibraryCard newCard = new LibraryCard();
                        newCard.setUser(user);
                        return newCard;
                    });

            LibraryCardDTO cardDto = dto.getLibraryCard();

            // Cập nhật cardNumber (kiểm tra trùng)
            if (cardDto.getCardNumber() != null && !cardDto.getCardNumber().equals(card.getCardNumber())) {
                if (cardRepo.existsByCardNumber(cardDto.getCardNumber())) {
                    throw new IllegalArgumentException("Số thẻ '" + cardDto.getCardNumber() + "' đã được sử dụng");
                }
                card.setCardNumber(cardDto.getCardNumber());
            } else if (card.getCardNumber() == null) {
                card.setCardNumber(generateUniqueCardNumber());
            }

            // Cập nhật ngày
            LocalDate startDate = cardDto.getStartDate() != null ? cardDto.getStartDate() : card.getStartDate();
            if (startDate == null) startDate = LocalDate.now();
            card.setStartDate(startDate);

            card.setEndDate(cardDto.getEndDate() != null ? cardDto.getEndDate() : startDate.plusYears(1));
            card.setNotes(cardDto.getNotes());

            if (cardDto.getStatus() != null && !cardDto.getStatus().isBlank()) {
                card.setStatus(LibraryCardStatus.valueOf(cardDto.getStatus().toUpperCase()));
            }

            cardRepo.save(card);
        }

        // Cập nhật mật khẩu
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPasswordHash(encoder.encode(dto.getPassword()));
        }

        return toDTO(userRepo.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy người dùng với ID: " + id);
        }

        cardRepo.deleteByUser_UserId(id);
        try {
            userRepo.deleteById(id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new IllegalStateException("Không thể xóa thành viên vì có giao dịch mượn/trả sách liên quan");
        }
    }

    @Override
    public void changePassword(String username, ChangePasswordRequest req) {
        UserAccount user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        user.setPasswordHash(encoder.encode(req.newPassword()));
        userRepo.save(user);
    }

    @Override
    public UserAccount me(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
    }

    @Override
    public UserAccount updateProfile(String username, ProfileUpdateRequest request) {
        UserAccount user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        user.setAddress(request.address());
        return userRepo.save(user);
    }
}