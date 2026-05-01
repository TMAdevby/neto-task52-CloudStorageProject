package com.example.netotask52cloudstorageproject.controller;

import com.example.netotask52cloudstorageproject.dto.LoginRequest;
import com.example.netotask52cloudstorageproject.dto.LoginResponse;
import com.example.netotask52cloudstorageproject.service.AuthService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Тесты")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("Должен вернуть 200 с токеном при успешном логине")
    void shouldReturn200WithTokenOnSuccessfulLogin() {
        LoginRequest request = new LoginRequest("testuser", "password");

        when(authService.login("testuser", "password")).thenReturn("test-token-123");

        // 🔹 ИЗМЕНЕНО: ResponseEntity<?> вместо ResponseEntity<LoginResponse>
        ResponseEntity<?> response = authController.login(request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof LoginResponse);
        assertEquals("test-token-123", ((LoginResponse) response.getBody()).getAuthToken());
    }

    @Test
    @DisplayName("Должен вернуть 400 при неверных учётных данных")
    void shouldReturn400OnInvalidCredentials() {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");

        when(authService.login("testuser", "wrongpassword"))
                .thenThrow(new IllegalArgumentException("Неверный логин или пароль"));

        // 🔹 ИЗМЕНЕНО: ResponseEntity<?> вместо ResponseEntity<LoginResponse>
        ResponseEntity<?> response = authController.login(request);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Должен вернуть 200 при успешном логауте")
    void shouldReturn200OnSuccessfulLogout() {
        String validToken = "valid-token-123";

        doNothing().when(authService).logout(validToken);

        ResponseEntity<Void> response = authController.logout(validToken);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Должен вернуть 401 при недействительном токене")
    void shouldReturn401OnInvalidToken() {
        String invalidToken = "invalid-token";

        doThrow(new IllegalArgumentException("Недействительный токен"))
                .when(authService).logout(invalidToken);

        ResponseEntity<Void> response = authController.logout(invalidToken);

        assertEquals(401, response.getStatusCodeValue());
    }
}