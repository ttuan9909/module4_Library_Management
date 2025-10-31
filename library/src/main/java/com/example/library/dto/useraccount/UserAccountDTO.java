package com.example.library.dto.useraccount;

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

    @NotNull(message = "Vui lòng chọn vai trò")
    private Long roleId;

    private String roleName;
}
