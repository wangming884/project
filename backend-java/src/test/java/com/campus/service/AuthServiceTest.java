package com.campus.service;

import com.campus.mapper.UserMapper;
import com.campus.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userMapper, passwordEncoder, jwtUtil);
        ReflectionTestUtils.setField(authService, "adminUsername", "admin");
        ReflectionTestUtils.setField(authService, "adminPassword", "Admin@123456");
    }

    @Test
    void loginReturnsAdminSessionForConfiguredAdminAccount() {
        when(jwtUtil.generateToken(0L)).thenReturn("admin-token");

        Map<String, Object> result = authService.login("admin", "Admin@123456");
        Map<?, ?> user = (Map<?, ?>) result.get("user");

        assertEquals("admin-token", result.get("token"));
        assertEquals(0, user.get("userId"));
        assertEquals("admin", user.get("username"));
        assertEquals("admin", user.get("role"));
        assertEquals("all", user.get("permissions"));
        verify(jwtUtil).generateToken(0L);
        verifyNoInteractions(userMapper, passwordEncoder);
    }

    @Test
    void loginRejectsInvalidPasswordForConfiguredAdminAccount() {
        RuntimeException error = assertThrows(RuntimeException.class,
            () -> authService.login("admin", "wrong-password"));

        assertEquals("管理员账号或密码错误", error.getMessage());
        verifyNoInteractions(userMapper, passwordEncoder, jwtUtil);
    }
}
