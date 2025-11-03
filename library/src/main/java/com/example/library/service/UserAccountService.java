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
        String cardNumber;
        boolean exists;
        final String PREFIX = "CARD-";
        
        do {
            int number = random.nextInt(1_000_000); 
            String sixDigitString = String.format("%06d", number);
            
            cardNumber = PREFIX + sixDigitString;
            
            exists = cardRepo.existsByCardNumber(cardNumber);
            
        } while (exists); 

        return cardNumber;
    }

    // ========================== CRUD ==========================
    
    @Override
    @Transactional(readOnly = true)
    public List<UserAccountDTO> getAllUsers() {
        return userRepo.findAllWithRole() 
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    

    @Override
    @Transactional(readOnly = true)
    public UserAccountDTO getUserById(Long id) { 
        UserAccount user = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        return toDTO(user);
    }

    @Override
    @Transactional
    public UserAccountDTO createUser(UserAccountDTO dto) {
        // 1. Validate trùng lặp cơ bản
        if (userRepo.findByUsername(dto.getUsername()).isPresent())
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        if (userRepo.existsByEmail(dto.getEmail()))
            throw new IllegalArgumentException("Email đã được sử dụng");
        if (userRepo.existsByPhoneNumber(dto.getPhoneNumber()))
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        
        // 2. VALIDATE ROLE
        Role targetRole;
        String roleNameFromDto = dto.getRoleName();
        
        if (roleNameFromDto != null && !roleNameFromDto.isBlank()) {
            targetRole = roleRepo.findByRoleName(roleNameFromDto)
                    .orElseThrow(() -> new IllegalArgumentException("Vai trò '" + roleNameFromDto + "' không tồn tại."));
        } else {
            // Sử dụng ROLE_READER mặc định (khuyến nghị dùng findByRoleNameIgnoreCase nếu đã thêm)
            targetRole = roleRepo.findByRoleName("ROLE_READER")
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò mặc định ROLE_READER. Vui lòng kiểm tra database để đảm bảo có bản ghi 'ROLE_READER'.")); 
        }

        // 3. VALIDATE STATUS
        UserStatus status;
        try {
            status = Optional.ofNullable(dto.getStatus()).filter(s -> !s.isBlank())
                .map(s -> UserStatus.valueOf(s.toUpperCase()))
                .orElse(UserStatus.ACTIVE);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Trạng thái '" + dto.getStatus() + "' không hợp lệ.");
        }

        // 4. LƯU USER
        UserAccount userToSave = toEntity(dto, targetRole, status);
        UserAccount savedUser = userRepo.save(userToSave);

        // 5. LƯU LIBRARY CARD (Bắt buộc)
        if (dto.getLibraryCard() == null) {
            throw new IllegalArgumentException("Thông tin thẻ thư viện không được để trống."); 
        }
        
        LibraryCard cardToSave = toLibraryCardEntity(dto.getLibraryCard());
        
        // GÁN CARD NUMBER TỰ ĐỘNG VÀ ĐẢM BẢO DUY NHẤT
        cardToSave.setCardNumber(generateUniqueCardNumber()); 
        
        cardToSave.setUser(savedUser); // Liên kết khóa ngoại
        
        // Gán giá trị mặc định cho startDate nếu Frontend bỏ trống
        if (cardToSave.getStartDate() == null) {
            cardToSave.setStartDate(LocalDate.now());
        }

        // ✨ LOGIC TỰ ĐỘNG GÁN END DATE (1 năm) CHO CREATE ✨
        if (cardToSave.getEndDate() == null) {
            cardToSave.setEndDate(cardToSave.getStartDate().plusYears(1)); 
        }
        
        cardRepo.save(cardToSave);

        return toDTO(savedUser);
    }

    @Override
    @Transactional
    public UserAccountDTO updateUser(Long id, UserAccountDTO dto) {
        UserAccount user = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));

        // Cập nhật thông tin cơ bản
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAvatarUrl(dto.getAvatarUrl());

        // Cập nhật ROLE
        if (dto.getRoleName() != null && !dto.getRoleName().isBlank() && 
            (user.getRole() == null || !dto.getRoleName().equals(user.getRole().getRoleName()))) {
            
            Role newRole = roleRepo.findByRoleName(dto.getRoleName())
                .orElseThrow(() -> new IllegalArgumentException("Vai trò '" + dto.getRoleName() + "' không tồn tại."));
            user.setRole(newRole);
        }

        // Cập nhật STATUS
        if (dto.getStatus() != null && !dto.getStatus().isBlank() &&
            (user.getStatus() == null || !user.getStatus().name().equalsIgnoreCase(dto.getStatus()))) { 
            try {
                UserStatus newStatus = UserStatus.valueOf(dto.getStatus().toUpperCase());
                user.setStatus(newStatus);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Trạng thái '" + dto.getStatus() + "' không hợp lệ.");
            }
        }
        
        // Cập nhật LIBRARY CARD
        if (dto.getLibraryCard() != null) {
            LibraryCard existingCard = cardRepo.findByUser_UserId(id)
                                             .orElseGet(LibraryCard::new); 
            
            LibraryCardDTO cardDto = dto.getLibraryCard();

            // 1. Kiểm tra ràng buộc DUY NHẤT cho CardNumber
            String newCardNumber = cardDto.getCardNumber();
            String currentCardNumber = existingCard.getCardNumber();

            if (newCardNumber != null && !newCardNumber.equals(currentCardNumber)) {
                if (cardRepo.existsByCardNumber(newCardNumber)) {
                    throw new IllegalArgumentException("Số thẻ '" + newCardNumber + "' đã được gán cho người dùng khác.");
                }
                existingCard.setCardNumber(newCardNumber);
            } else if (newCardNumber == null && existingCard.getCardNumber() == null) {
                 existingCard.setCardNumber(generateUniqueCardNumber());
            }

            // 2. Cập nhật các trường ngày tháng và Status
            
            // Lấy StartDate mới từ DTO hoặc giữ nguyên giá trị cũ
            LocalDate newStartDate = cardDto.getStartDate() != null ? cardDto.getStartDate() : existingCard.getStartDate();
            
            // Đảm bảo newStartDate không null
            if (newStartDate == null) {
                newStartDate = LocalDate.now();
            }
            existingCard.setStartDate(newStartDate);
            
            // ✨ LOGIC TỰ ĐỘNG GÁN END DATE (1 năm) CHO UPDATE (ĐÃ XÓA LỖI VALIDATION THỦ CÔNG) ✨
            if (cardDto.getEndDate() == null) {
                // Nếu Frontend KHÔNG gửi EndDate (người dùng bỏ trống), tự động tính 1 năm từ StartDate
                existingCard.setEndDate(newStartDate.plusYears(1)); 
            } else {
                // Nếu Frontend gửi EndDate lên (người dùng cố ý nhập), sử dụng giá trị đó
                existingCard.setEndDate(cardDto.getEndDate());
            }

            existingCard.setNotes(cardDto.getNotes());
            
            // Cập nhật Status
            if (cardDto.getStatus() != null && !cardDto.getStatus().isBlank()) {
                existingCard.setStatus(LibraryCardStatus.valueOf(cardDto.getStatus().toUpperCase()));
            }

            existingCard.setUser(user); 
            cardRepo.save(existingCard);
        }

        // Cập nhật mật khẩu nếu có nhập
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPasswordHash(encoder.encode(dto.getPassword()));
        }

        UserAccount updated = userRepo.save(user);
        return toDTO(updated);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy người dùng để xóa");
        }
        
        cardRepo.deleteByUser_UserId(id); 
        
        try {
            userRepo.deleteById(id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new IllegalStateException("Không thể xóa thành viên vì thành viên này đang có các giao dịch liên quan (mượn/trả sách) chưa được xử lý.");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi không xác định khi xóa User ID " + id + ": " + e.getMessage());
        }
    }

    // Các phương thức khác giữ nguyên
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