package com.example.library.controller;

import com.example.library.dto.request.CreateAdminDto;
import com.example.library.entity.Employee;
import com.example.library.entity.Role;
import com.example.library.entity.enums.UserStatus;
import com.example.library.repository.EmployeeRepository;
import com.example.library.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final EmployeeRepository employeeRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepo;

    @PostMapping("/create")
    // @PreAuthorize("hasRole('ADMIN')") // bật khi chỉ admin được tạo admin
    public String createAdmin(@RequestBody CreateAdminDto dto) {
        if (employeeRepo.findByUsername(dto.username()).isPresent()) {
            return "Username already exists";
        }
        Role adminRole = roleRepo.findById(1L).orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Role ID 1 trong DB!"));
        Employee emp = new Employee();
        emp.setUsername(dto.username());
        emp.setPassword(passwordEncoder.encode(dto.password()));
        // set role appropriately
        emp.setRole(adminRole);
        emp.setStatus(UserStatus.ACTIVE);
        emp.setFullName(dto.fullName());
        employeeRepo.save(emp);
        return "Admin created";
    }
}
