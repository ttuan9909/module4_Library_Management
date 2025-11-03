package com.example.library.service;

import com.example.library.dto.request.ChangePasswordRequest;
import com.example.library.dto.request.ProfileUpdateRequest;
import com.example.library.dto.request.UserAccountDTO;
import com.example.library.entity.Role;
import com.example.library.entity.UserAccount;
import com.example.library.entity.enums.UserStatus;
import com.example.library.repository.RoleRepository;
import com.example.library.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAccountService implements IUserAccountService {

    private final UserAccountRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;

    // ========================== DTO CONVERTERS ==========================
    private UserAccountDTO toDTO(UserAccount entity) {
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
                // Đã sửa lỗi: Kiểm tra NULL cho Status (ENUM) an toàn
                .status(entity.getStatus() != null ? entity.getStatus().name() : "UNKNOWN_STATUS")
                .build();
    }

    // Cập nhật toEntity để nhận cả Role và Status đã được kiểm tra (VALIDATED)
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
                .role(role) // Sử dụng role đã được kiểm tra
                .status(status) // Sử dụng status đã được kiểm tra
                .build();
    }

    // ========================== CRUD ==========================
    @Override
    @Transactional
    public List<UserAccountDTO> getAllUsers() {
        // Sử dụng findAllWithRole() - Đã sửa lỗi Lazy Initialization
        return userRepo.findAllWithRole() 
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    

    @Override
    @Transactional
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

        // 2. VALIDATE ROLE (Kiểm tra sự tồn tại của Role name)
        Role targetRole;
        if (dto.getRoleName() != null && !dto.getRoleName().isBlank()) {
            targetRole = roleRepo.findByRoleName(dto.getRoleName())
                    .orElseThrow(() -> new IllegalArgumentException("Vai trò '" + dto.getRoleName() + "' không tồn tại."));
        } else {
            // Gán role mặc định nếu không có
            targetRole = roleRepo.findByRoleName("READER")
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò mặc định READER"));
        }

        // 3. VALIDATE STATUS (Kiểm tra và chuyển đổi ENUM)
        UserStatus status;
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            try {
                status = UserStatus.valueOf(dto.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Trạng thái '" + dto.getStatus() + "' không hợp lệ.");
            }
        } else {
            // Gán trạng thái mặc định
            status = UserStatus.ACTIVE;
        }

        UserAccount saved = userRepo.save(toEntity(dto, targetRole, status));
        return toDTO(saved);
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

        // Cập nhật ROLE (Chỉ khi DTO có cung cấp roleName mới)
        if (dto.getRoleName() != null && !dto.getRoleName().isBlank()) {
             // Thêm check khác role để tránh gọi DB/tìm kiếm role nếu role không đổi
             if (user.getRole() == null || !dto.getRoleName().equals(user.getRole().getRoleName())) {
                Role newRole = roleRepo.findByRoleName(dto.getRoleName())
                    .orElseThrow(() -> new IllegalArgumentException("Vai trò '" + dto.getRoleName() + "' không tồn tại."));
                user.setRole(newRole);
             }
        }

        // Cập nhật STATUS (Chỉ khi DTO có cung cấp status mới)
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
             // Thêm check khác status để tránh gọi ENUM.valueOf nếu status không đổi
             if (user.getStatus() == null || !dto.getStatus().equals(user.getStatus().name())) {
                 try {
                     UserStatus newStatus = UserStatus.valueOf(dto.getStatus().toUpperCase());
                     user.setStatus(newStatus);
                 } catch (IllegalArgumentException e) {
                     throw new IllegalArgumentException("Trạng thái '" + dto.getStatus() + "' không hợp lệ.");
                 }
             }
        }

        // Nếu user nhập mật khẩu mới → cập nhật
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPasswordHash(encoder.encode(dto.getPassword()));
        }

        UserAccount updated = userRepo.save(user);
        return toDTO(updated);
    }

    @Override
public void deleteUser(Long id) {
    if (!userRepo.existsById(id)) {
        throw new IllegalArgumentException("Không tìm thấy người dùng để xóa");
    }
    
    // ⭐️ BƯỚC CẢI TIẾN: Xử lý Lỗi Ràng buộc Khóa Ngoại tại đây (Tùy chọn)
    // Nếu UserAccount có liên quan tới các giao dịch (ví dụ: BookLending),
    // bạn phải xóa các giao dịch đó trước nếu không dùng ON DELETE CASCADE.
    // Ví dụ: 
    // lendingRepo.deleteByUserId(id); 
    
    try {
        userRepo.deleteById(id);
    } catch (org.springframework.dao.DataIntegrityViolationException e) {
        // Log lỗi chi tiết nếu bạn muốn
        // log.error("Lỗi ràng buộc khi xóa User ID {}: {}", id, e.getMessage()); 
        throw new IllegalStateException("Không thể xóa thành viên vì thành viên này đang có các giao dịch liên quan (mượn/trả sách). Vui lòng xử lý các giao dịch trước.");
    } catch (Exception e) {
         // Xử lý các lỗi khác
         throw new RuntimeException("Lỗi không xác định khi xóa User ID " + id + ": " + e.getMessage());
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