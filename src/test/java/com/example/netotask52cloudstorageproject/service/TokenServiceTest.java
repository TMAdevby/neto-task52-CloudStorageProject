package com.example.netotask52cloudstorageproject.service;

import com.example.netotask52cloudstorageproject.model.AuthToken;
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

@DisplayName("TokenService Тесты")
class TokenServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Когда генерируется токен")
    class TokenGeneration {

        @Test
        @DisplayName("Должен сгенерировать уникальный токен")
        void shouldGenerateUniqueToken() {

            User testUser = new User();
            testUser.setId(1L);
            testUser.setLogin("testuser");

            String token1 = tokenService.generateToken(testUser);
            String token2 = tokenService.generateToken(testUser);

            assertNotNull(token1, "Токен не должен быть null");
            assertNotNull(token2, "Токен не должен быть null");

            assertNotEquals(token1, token2, "Токены должны быть уникальными");

            assertEquals(32, token1.length(), "Длина токена должна быть 32 символа");
        }

        @Test
        @DisplayName("Должен сохранить токен в хранилище")
        void shouldStoreTokenInMemory() {

            User testUser = new User();
            testUser.setId(1L);
            testUser.setLogin("testuser");

            String token = tokenService.generateToken(testUser);

            Optional<AuthToken> storedToken = tokenService.getToken(token);

            assertTrue(storedToken.isPresent(), "Токен должен быть сохранён");
            assertEquals(testUser.getId(), storedToken.get().getUser().getId());
            assertTrue(storedToken.get().isValid(), "Токен должен быть валиден");
        }
    }

    @Nested
    @DisplayName("Когда валидируется токен")
    class TokenValidation {

        @Test
        @DisplayName("Должен вернуть пользователя для валидного токена")
        void shouldReturnUserForValidToken() {

            User testUser = new User();
            testUser.setId(1L);
            testUser.setLogin("testuser");

            String token = tokenService.generateToken(testUser);

            Optional<User> result = tokenService.validateToken(token);

            assertTrue(result.isPresent(), "Пользователь должен быть найден");
            assertEquals(testUser.getId(), result.get().getId());
        }

        @Test
        @DisplayName("Должен вернуть пустой Optional для несуществующего токена")
        void shouldReturnEmptyForNonExistentToken() {

            Optional<User> result = tokenService.validateToken("non-existent-token");

            assertFalse(result.isPresent(), "Пользователь не должен быть найден");
        }

        @Test
        @DisplayName("Должен вернуть пустой Optional для отозванного токена")
        void shouldReturnEmptyForInvalidatedToken() {

            User testUser = new User();
            testUser.setId(1L);

            String token = tokenService.generateToken(testUser);

            tokenService.invalidateToken(token);

            Optional<User> result = tokenService.validateToken(token);

            assertFalse(result.isPresent(), "Отозванный токен должен быть недействителен");
        }
    }


    @Nested
    @DisplayName("Когда отзывается токен")
    class TokenInvalidation {

        @Test
        @DisplayName("Должен сделать токен недействительным")
        void shouldInvalidateToken() {

            User testUser = new User();
            testUser.setId(1L);

            String token = tokenService.generateToken(testUser);

            Optional<AuthToken> beforeInvalidation = tokenService.getToken(token);
            assertTrue(beforeInvalidation.get().isValid());

            tokenService.invalidateToken(token);

            Optional<AuthToken> afterInvalidation = tokenService.getToken(token);
            assertFalse(afterInvalidation.get().isValid(), "Токен должен быть недействителен");
        }

        @Test
        @DisplayName("Не должен выбрасывать исключение для несуществующего токена")
        void shouldNotThrowForNonExistentToken() {

            assertDoesNotThrow(
                    () -> tokenService.invalidateToken("non-existent-token"),
                    "Не должно быть исключений для несуществующего токена"
            );
        }
    }
}
