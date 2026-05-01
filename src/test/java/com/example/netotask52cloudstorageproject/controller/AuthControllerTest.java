package com.example.netotask52cloudstorageproject.controller;

import com.example.netotask52cloudstorageproject.dto.LoginRequest;
import com.example.netotask52cloudstorageproject.dto.LoginResponse;
import com.example.netotask52cloudstorageproject.service.AuthService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuthController Тесты")
class AuthControllerTest {

    @MockBean
    private AuthService authService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authService);
    }

    @Nested
    @DisplayName("Когда выполняется логин")
    class Login {

        @Test
        @DisplayName("Должен вернуть 200 с токеном при успешном логине")
        void shouldReturn200WithTokenOnSuccessfulLogin() {

            LoginRequest request = new LoginRequest("testuser", "password");

            when(authService.login("testuser", "password")).thenReturn("test-token-123");

            ResponseEntity<LoginResponse> response = authController.login(request);

            assertEquals(200, response.getStatusCodeValue());
            assertNotNull(response.getBody());
            assertEquals("test-token-123", response.getBody().getAuthToken());
        }

        @Test
        @DisplayName("Должен вернуть 400 при неверных учётных данных")
        void shouldReturn400OnInvalidCredentials() {

            LoginRequest request = new LoginRequest("testuser", "wrongpassword");

            when(authService.login("testuser", "wrongpassword"))
                    .thenThrow(new IllegalArgumentException("Неверный логин или пароль"));

            ResponseEntity<LoginResponse> response = authController.login(request);

            assertEquals(400, response.getStatusCodeValue());
        }
    }

    @Nested
    @DisplayName("Когда выполняется логаут")
    class Logout {

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
}
