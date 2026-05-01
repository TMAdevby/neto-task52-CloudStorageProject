package com.example.netotask52cloudstorageproject.service;

import com.example.netotask52cloudstorageproject.model.User;
import com.example.netotask52cloudstorageproject.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

@DisplayName("AuthService Тесты")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Когда логин успешный")
    class SuccessfulLogin {

        @Test
        @DisplayName("Должен вернуть токен при правильных учётных данных")
        void shouldReturnTokenWhenCredentialsAreValid() {
            User testUser = new User();
            testUser.setId(1L);
            testUser.setLogin("testuser");
            testUser.setPasswordHash("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");

            when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(testUser));
            when(tokenService.generateToken(testUser)).thenReturn("test-token-123");

            String token = authService.login("testuser", "password");

            assertNotNull(token);
            assertEquals("test-token-123", token);
            verify(userRepository, times(1)).findByLogin("testuser");
            verify(tokenService, times(1)).generateToken(testUser);
        }
    }

    @Nested
    @DisplayName("Когда логин неуспешный")
    class FailedLogin {

        @Test
        @DisplayName("Должен выбросить исключение, если пользователь не найден")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findByLogin("nonexistent")).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.login("nonexistent", "password")
            );

            assertEquals("Неверный логин или пароль", exception.getMessage());
            verify(tokenService, never()).generateToken(any());
        }

        @Test
        @DisplayName("Должен выбросить исключение, если пароль неверный")
        void shouldThrowExceptionWhenPasswordIsWrong() {
            User testUser = new User();
            testUser.setId(1L);
            testUser.setLogin("testuser");
            testUser.setPasswordHash("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");

            when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(testUser));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.login("testuser", "wrongpassword")
            );

            assertEquals("Неверный логин или пароль", exception.getMessage());
            verify(tokenService, never()).generateToken(any());
        }
    }

    @Nested
    @DisplayName("Когда выполняется логаут")
    class Logout {

        @Test
        @DisplayName("Должен отозвать токен при успешном логауте")
        void shouldInvalidateTokenOnSuccessfulLogout() {
            User testUser = new User();
            testUser.setId(1L);

            String validToken = "valid-token-123";

            when(tokenService.validateToken(validToken)).thenReturn(Optional.of(testUser));

            authService.logout(validToken);

            verify(tokenService, times(1)).invalidateToken(validToken);
        }

        @Test
        @DisplayName("Должен выбросить исключение, если токен недействителен")
        void shouldThrowExceptionWhenTokenIsInvalid() {
            when(tokenService.validateToken("invalid-token")).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.logout("invalid-token")
            );

            assertEquals("Недействительный токен", exception.getMessage());
            verify(tokenService, never()).invalidateToken(any());
        }
    }
}