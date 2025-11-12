package com.example.library.service;

import com.example.library.dto.request.LibraryCardDTO;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAccountService implements IUserAccountService {
    private final UserAccountRepository userRepo;
    private final RoleRepository roleRepo;
    private final LibraryCardRepository cardRepo;
    private final PasswordEncoder encoder;
    private final Random random = new Random();

    @Override
    @Transactional(readOnly = true)
    public List<UserAccountDTO> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(this::toDTV)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccountDTO getUserById(Long id) {
        UserAccount user = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + id));
        return toDTV(user);
    }

    @Override
    @Transactional
    public UserAccountDTO createUser(UserAccountDTO dto) {
        // Validate required fields
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        if (userRepo.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }
        if (dto.getEmail() != null && userRepo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }
        if (dto.getPhoneNumber() != null && userRepo.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }

        // Role
        String roleName = Optional.ofNullable(dto.getRoleName())
                .filter(r -> !r.isBlank())
                .orElse("ROLE_READER");
//        Role role = roleRepo.findByRoleName(roleName)
//                .orElseThrow(() -> new IllegalArgumentException("Vai trò không tồn tại: " + roleName));
        Role role = Optional.ofNullable(roleRepo.findByRoleName(roleName))
                .orElseThrow(() -> new IllegalArgumentException("Vai trò không tồn tại: " + roleName));

        // Status
        UserStatus status = Optional.ofNullable(dto.getStatus())
                .filter(s -> !s.isBlank())
                .map(s -> UserStatus.valueOf(s.toUpperCase()))
                .orElse(UserStatus.ACTIVE);

        // Create user
        UserAccount user = UserAccount.builder()
                .username(dto.getUsername())
                .passwordHash(encoder.encode(dto.getPassword()))
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .dateOfBirth(dto.getDateOfBirth())
                .avatarUrl(dto.getAvatarUrl())
                .role(role)
                .status(status)
                .build();

        UserAccount savedUser = userRepo.save(user);

        // Create LibraryCard
        LibraryCard card = new LibraryCard();
        card.setUser(savedUser);
        card.setCardNumber(generateUniqueCardNumber());
        card.setStartDate(dto.getLibraryCard() != null && dto.getLibraryCard().getStartDate() != null
                ? dto.getLibraryCard().getStartDate() : LocalDate.now());
        card.setEndDate(dto.getLibraryCard() != null && dto.getLibraryCard().getEndDate() != null
                ? dto.getLibraryCard().getEndDate() : card.getStartDate().plusYears(1));
        card.setNotes(dto.getLibraryCard() != null ? dto.getLibraryCard().getNotes() : null);
        card.setStatus(dto.getLibraryCard() != null && dto.getLibraryCard().getStatus() != null
                ? LibraryCardStatus.valueOf(dto.getLibraryCard().getStatus().toUpperCase())
                : LibraryCardStatus.ACTIVE);

        cardRepo.save(card);

        return toDTV(savedUser);
    }

    @Override
    @Transactional
    public UserAccountDTO updateUser(Long id, UserAccountDTO dto) {
        UserAccount user = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + id));

        // Cập nhật thông tin cơ bản
        user.setUsername(dto.getUsername());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAvatarUrl(dto.getAvatarUrl());

        // Role
        if (dto.getRoleName() != null && !dto.getRoleName().equals(user.getRole().getRoleName())) {
//            Role role = roleRepo.findByRoleName(dto.getRoleName())
//                    .orElseThrow(() -> new IllegalArgumentException("Vai trò không tồn tại"));
            Role role = Optional.ofNullable(roleRepo.findByRoleName(dto.getRoleName()))
                    .orElseThrow(() -> new IllegalArgumentException("Vai trò không tồn tại" ));
            user.setRole(role);
        }

        // Status
        if (dto.getStatus() != null && !dto.getStatus().equalsIgnoreCase(user.getStatus().name())) {
            user.setStatus(UserStatus.valueOf(dto.getStatus().toUpperCase()));
        }

        // Password
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            user.setPasswordHash(encoder.encode(dto.getPassword()));
        }

        // LibraryCard
        LibraryCard card = cardRepo.findByUser_UserId(id)
                .orElseGet(() -> {
                    LibraryCard newCard = new LibraryCard();
                    newCard.setUser(user);
                    return newCard;
                });

        if (dto.getLibraryCard() != null) {
            LibraryCardDTO cardDto = dto.getLibraryCard();
            if (card.getCardNumber() == null || cardDto.getCardNumber() == null) {
                card.setCardNumber(generateUniqueCardNumber());
            }
            card.setStartDate(cardDto.getStartDate() != null ? cardDto.getStartDate() : LocalDate.now());
            card.setEndDate(cardDto.getEndDate() != null ? cardDto.getEndDate() : card.getStartDate().plusYears(1));
            card.setNotes(cardDto.getNotes());
            card.setStatus(cardDto.getStatus() != null
                    ? LibraryCardStatus.valueOf(cardDto.getStatus().toUpperCase())
                    : LibraryCardStatus.ACTIVE);
        }

        cardRepo.save(card);
        userRepo.save(user);

        return toDTV(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy người dùng với ID: " + id);
        }
        try {
            cardRepo.deleteByUser_UserId(id);
            userRepo.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Không thể xóa thành viên vì đang có giao dịch mượn/trả sách!");
        }
    }

    private LibraryCardDTO toLibraryCardDTO(LibraryCard card) {
        if (card == null) return null;
        return LibraryCardDTO.builder()
                .cardNumber(card.getCardNumber())
                .startDate(card.getStartDate())
                .endDate(card.getEndDate())
                .status(card.getStatus() != null ? card.getStatus().name() : "ACTIVE")
                .notes(card.getNotes())
                .build();
    }

    private LibraryCard toLibraryCardEntity(LibraryCardDTO dto, UserAccount user) {
        if (dto == null) return null;
        LibraryCard card = new LibraryCard();
        card.setUser(user);
        card.setCardNumber(dto.getCardNumber());
        card.setStartDate(dto.getStartDate());
        card.setEndDate(dto.getEndDate());
        card.setNotes(dto.getNotes());
        card.setStatus(Optional.ofNullable(dto.getStatus())
                .map(s -> LibraryCardStatus.valueOf(s.toUpperCase()))
                .orElse(LibraryCardStatus.ACTIVE));
        return card;
    }

    private UserAccountDTO toDTV(UserAccount entity) {
        LibraryCard card = cardRepo.findByUser_UserId(entity.getUserId()).orElse(null);
        return UserAccountDTO.builder()
                .userId(entity.getUserId())
                .username(entity.getUsername())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .dateOfBirth(entity.getDateOfBirth())
                .avatarUrl(entity.getAvatarUrl())
                .roleName(entity.getRole() != null ? entity.getRole().getRoleName() : "ROLE_READER")
                .status(entity.getStatus() != null ? entity.getStatus().name() : "ACTIVE")
                .libraryCard(toLibraryCardDTO(card))
                .build();
    }

    // ========================== GENERATE CARD NUMBER ==========================

    private String generateUniqueCardNumber() {
        String prefix = "CARD-";
        for (int i = 0; i < 50; i++) {
            String number = prefix + String.format("%06d", random.nextInt(1_000_000));
            if (!cardRepo.existsByCardNumber(number)) {
                return number;
            }
        }
        return prefix + System.currentTimeMillis() % 1_000_000; // Fallback
    }

}
