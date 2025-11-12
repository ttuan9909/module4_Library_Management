package com.example.library.config;

import com.example.library.security.JwtAuthenticationFilter;
import com.example.library.security.LibraryUserDetailsService;
import com.example.library.security.RoleBasedAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
//    private final JwtAuthenticationFilter jwtFilter;
    private final LibraryUserDetailsService userDetailsService;
    private final RoleBasedAuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers("/api/**", "/uploads/**")
//                )
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/auth/**","/api/auth/**", "/error",
                                "/css/**", "/js/**", "/images/**", "/assets/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/uploads/avatar").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")           // Bảo Spring đây là trang login
                        .loginProcessingUrl("/auth/login")  // URL mà form HTML sẽ POST tới
                        .successHandler(successHandler)     // <--- GỌI HANDLER KHI THÀNH CÔNG
                        .failureUrl("/auth/login?error=true") // Nếu sai, quay lại đây
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout") // URL mà form logout sẽ POST tới
                        .logoutSuccessUrl("/auth/login?logout=true") // Logout xong về đây
                        .invalidateHttpSession(true) // Xóa session
                        .deleteCookies("JSESSIONID") // Xóa cookie
                        .permitAll()
                )

                .authenticationProvider(authProvider());
        return http.build();
    }

    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
