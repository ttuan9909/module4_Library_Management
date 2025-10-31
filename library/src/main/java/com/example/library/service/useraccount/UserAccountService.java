package com.example.library.service.useraccount;

import com.example.library.repository.role.RoleRepository;
import com.example.library.dto.request.ChangePasswordRequest;
import com.example.library.dto.request.ProfileUpdateRequest;
import com.example.library.dto.useraccount.UserAccountDTO;
import com.example.library.entity.Role;
import com.example.library.entity.UserAccount;
import com.example.library.entity.enums.UserStatus;
import com.example.library.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
                .roleId(entity.getRole().getRoleId())
                .roleName(entity.getRole().getRoleName())
                .build();
    }

    private UserAccount toEntity(UserAccountDTO dto, Role role) {
        return UserAccount.builder()
                .userId(dto.getUserId())
                .fullName(dto.getFullName())
                .username(dto.getUsername())
                .passwordHash(encoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .dateOfBirth(dto.getDateOfBirth())
                .avatarUrl(dto.getAvatarUrl())
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
    }

    // ========================== CRUD ==========================
    @Override
    public List<UserAccountDTO> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserAccountDTO getUserById(Long id) {
        UserAccount user = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        return toDTO(user);
    }

    @Override
    @Transactional
    public UserAccountDTO createUser(UserAccountDTO dto) {
        // Kiểm tra trùng
        if (userRepo.findByUsername(dto.getUsername()).isPresent())
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        if (userRepo.findByEmail(dto.getEmail()).isPresent())
            throw new IllegalArgumentException("Email đã được sử dụng");
        if (userRepo.findByPhoneNumber(dto.getPhoneNumber()).isPresent())
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");

        Role role = roleRepo.findById(dto.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò"));

        UserAccount saved = userRepo.save(toEntity(dto, role));
        return toDTO(saved);
    }

    @Override
    @Transactional
    public UserAccountDTO updateUser(Long id, UserAccountDTO dto) {
        UserAccount user = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAvatarUrl(dto.getAvatarUrl());

        if (dto.getRoleId() != null) {
            Role role = roleRepo.findById(dto.getRoleId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò"));
            user.setRole(role);
        }

        UserAccount updated = userRepo.save(user);
        return toDTO(updated);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy người dùng để xóa");
        }
        userRepo.deleteById(id);
    }

    @Override
    public void changePassword(String username, ChangePasswordRequest req) {
        UserAccount user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));

        // ⚠️ Không kiểm tra mật khẩu cũ nữa
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

        // Cập nhật thông tin người dùng
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        user.setAddress(request.address());
        return userRepo.save(user);
    }
}