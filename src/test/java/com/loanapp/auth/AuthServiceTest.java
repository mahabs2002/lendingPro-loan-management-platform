package com.loanapp.auth;

import com.loanapp.audit.AuditLogService;
import com.loanapp.auth.dto.*;
import com.loanapp.entity.User;
import com.loanapp.enums.Role;
import com.loanapp.exception.BadRequestException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private CustomUserDetailsService userDetailsService;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuditLogService auditLogService;
    @InjectMocks private AuthService authService;

    @Test
    void register_ShouldSucceed_WhenEmailIsNew() {
        RegisterRequest req = new RegisterRequest();
        req.setFullName("Test User"); req.setEmail("test@example.com");
        req.setMobile("9876543210"); req.setPassword("password123");
        req.setRole(Role.USER);

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.existsByMobile("9876543210")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");

        User savedUser = User.builder().id(1L).fullName("Test User")
                .email("test@example.com").role(Role.USER).password("encoded_password").build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        var mockDetails = mock(org.springframework.security.core.userdetails.UserDetails.class);
        when(mockDetails.getUsername()).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(mockDetails);
        when(jwtUtil.generateToken(any())).thenReturn("mocked_token");
        doNothing().when(auditLogService).log(any(), any(), any(), any(), any(), any(), any());

        AuthResponse response = authService.register(req);
        assertThat(response.getToken()).isEqualTo("mocked_token");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void register_ShouldThrow_WhenEmailExists() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("dup@example.com"); req.setMobile("9876543210");
        when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);
        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email already registered");
    }
}
