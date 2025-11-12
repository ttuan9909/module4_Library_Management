package com.example.library.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (response.isCommitted()) {
            return;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isAdmin = authorities.stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        // Logic chuyển hướng
        if (isAdmin) {
            response.sendRedirect("/admin"); // Lái về trang admin
        } else {
            response.sendRedirect("/"); // Lái về trang user (trang chủ)
        }
    }
}
