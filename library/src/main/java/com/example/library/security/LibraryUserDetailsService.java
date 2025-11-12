package com.example.library.security;

import com.example.library.entity.Employee;
import com.example.library.entity.UserAccount;
import com.example.library.repository.EmployeeRepository;
import com.example.library.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryUserDetailsService implements UserDetailsService {
    private final UserAccountRepository userRepo;
    private final EmployeeRepository employeeRepo;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1) thử tìm trong employee (admin)
        var empOpt = employeeRepo.findByUsername(username);
        if (empOpt.isPresent()) {
            Employee emp = empOpt.get();
            // lấy role của employee (tùy cấu trúc Role trong entity của bạn)
            String roleName = emp.getRole() == null ? "ROLE_ADMIN" :
                    // nếu Role là entity với field roleName:
                    (emp.getRole().getRoleName() != null ? emp.getRole().getRoleName() : "ROLE_ADMIN");
            return org.springframework.security.core.userdetails.User.builder()
                    .username(emp.getUsername())
                    .password(emp.getPassword()) // phải là hash (BCrypt)
                    .authorities(new SimpleGrantedAuthority(roleName))
                    .accountLocked("LOCKED".equalsIgnoreCase(emp.getStatus().name()))
                    .disabled("DISABLED".equalsIgnoreCase(emp.getStatus().name()))
                    .build();
        }

        // 2) không có employee -> tìm user account (reader)
        UserAccount u = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String roleName = u.getRole().getRoleName(); // giữ như code bạn đang dùng
        return org.springframework.security.core.userdetails.User.builder()
                .username(u.getUsername())
                .password(u.getPasswordHash())
                .authorities(new SimpleGrantedAuthority(roleName))
                .accountLocked("LOCKED".equalsIgnoreCase(u.getStatus().name()))
                .disabled("DISABLED".equalsIgnoreCase(u.getStatus().name()))
                .build();
    }
}
