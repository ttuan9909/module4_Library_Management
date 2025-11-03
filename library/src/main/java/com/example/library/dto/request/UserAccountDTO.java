package com.example.library.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccountDTO {

    private Long userId;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 4, max = 50, message = "Tên đăng nhập phải từ 4–50 ký tự")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @Email(message = "Email không hợp lệ")
    private String email;

    @Pattern(regexp = "^\\d{10,11}$", message = "Số điện thoại phải có 10–11 chữ số")
    private String phoneNumber;

    private String address;
    private LocalDate dateOfBirth;
    private String avatarUrl;
    
    @Pattern(regexp = "ROLE_ADMIN|ROLE_READER", message = "Vai trò không hợp lệ.")
    private String roleName;
    
    @Pattern(regexp = "ACTIVE|LOCKED|DISABLED", message = "Trạng thái không hợp lệ. Chỉ chấp nhận ACTIVE, LOCKED, DISABLED.")
    private String status;

    // ✨ TRƯỜNG MỚI: Thông tin thẻ thư viện ✨
    // Khi thêm mới, trường này nên được điền
    @NotNull(message = "Thông tin thẻ thư viện không được để trống.")
    private LibraryCardDTO libraryCard; 
}