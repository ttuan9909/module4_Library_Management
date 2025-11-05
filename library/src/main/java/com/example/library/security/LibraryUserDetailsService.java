//package com.example.library.security;
//
//import com.example.library.entity.UserAccount;
//import com.example.library.repository.UserAccountRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class LibraryUserDetailsService implements UserDetailsService {
//    private final UserAccountRepository userRepo;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        UserAccount u = userRepo.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//        // map role_name -> GrantedAuthority
//        String roleName = u.getRole().getRoleName(); // e.g., ROLE_ADMIN / ROLE_READER
//        return User.builder()
//                .username(u.getUsername())
//                .password(u.getPasswordHash())
//                .authorities(List.of(new SimpleGrantedAuthority(roleName)))
//                .accountLocked("LOCKED".equalsIgnoreCase(u.getStatus().name()))
//                .disabled("DISABLED".equalsIgnoreCase(u.getStatus().name()))
//                .build();
//    }
//}
