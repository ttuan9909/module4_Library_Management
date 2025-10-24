package com.example.library.controller;

import com.example.library.dto.request.LoginRequest;
import com.example.library.dto.request.RegisterRequest;
import com.example.library.dto.response.AuthResponse;
import com.example.library.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class WebAuthController {
    private final AuthService authService;

    // ===== REGISTER =====
    @GetMapping("/register")
    public String registerForm(Model model,
                               @RequestParam(name = "admin", defaultValue = "false") boolean admin) {
        model.addAttribute("registerRequest", new RegisterRequest(null, null, null, null, null));
        model.addAttribute("admin", admin);
        return "auth/register"; // templates/auth/register.html
    }

    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("registerRequest") RegisterRequest req,
                             BindingResult binding,
                             @RequestParam(name = "admin", defaultValue = "false") boolean admin,
                             Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("admin", admin);
            return "auth/register";
        }
        try {
            authService.register(req, admin);
            // Đăng ký xong -> chuyển sang trang login
            return "redirect:/auth/login?registered";
        } catch (IllegalArgumentException ex) {
            // lỗi trùng username/email, v.v...
            model.addAttribute("admin", admin);
            model.addAttribute("serverError", ex.getMessage());
            return "auth/register";
        } catch (Exception ex) {
            model.addAttribute("admin", admin);
            model.addAttribute("serverError", "Register failed");
            return "auth/register";
        }
    }

    // ===== LOGIN =====
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest(null, null));
        return "auth/login"; // templates/auth/login.html
    }

    @PostMapping("/login")
    public String doLogin(@Valid @ModelAttribute("loginRequest") LoginRequest req,
                          BindingResult binding,
                          Model model,
                          @CookieValue(name = "LIB_JWT", required = false) String existingJwt,
                          @RequestHeader(value = "Referer", required = false) String referer,
                          @RequestHeader(value = "Host", required = false) String host,
                          @RequestHeader(value = "X-Forwarded-Proto", required = false) String scheme,
                          org.springframework.http.HttpHeaders headers,
                          jakarta.servlet.http.HttpServletResponse resp) {
        if (binding.hasErrors()) {
            return "auth/login";
        }
        try {
            AuthResponse auth = authService.login(req);

            ResponseCookie cookie = ResponseCookie.from("LIB_JWT", auth.token())
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofHours(8))
                    .sameSite("Lax")
                    .build();
            resp.addHeader("Set-Cookie", cookie.toString());

            return "redirect:/";
        } catch (org.springframework.security.authentication.BadCredentialsException ex) {
            model.addAttribute("serverError", "Sai username hoặc password");
            return "auth/login";
        } catch (Exception ex) {
            model.addAttribute("serverError", "Login failed");
            return "auth/login";
        }
    }

    // ===== LOGOUT (xóa cookie) =====
    @PostMapping("/logout")
    public String logout(jakarta.servlet.http.HttpServletResponse resp) {
        ResponseCookie cookie = ResponseCookie.from("LIB_JWT", "")
                .httpOnly(true).path("/").maxAge(0).sameSite("Lax").build();
        resp.addHeader("Set-Cookie", cookie.toString());
        return "redirect:/auth/login?logout";
    }
}
