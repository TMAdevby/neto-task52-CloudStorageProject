package com.example.netotask52cloudstorageproject.repository;

import com.example.netotask52cloudstorageproject.model.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

@Testcontainers

@DataJpaTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("UserRepository Интеграционные тесты")
class UserRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);

        registry.add("spring.liquibase.enabled", () -> false);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Должен сохранить пользователя в БД")
    void shouldSaveUserToDatabase() {

        User user = new User();
        user.setLogin("testuser");
        user.setPasswordHash("hashed-password");

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId(), "ID должен быть сгенерирован");
        assertEquals("testuser", savedUser.getLogin());
    }

    @Test
    @DisplayName("Должен найти пользователя по логину")
    void shouldFindUserByLogin() {

        User user = new User();
        user.setLogin("findme");
        user.setPasswordHash("hashed-password");
        userRepository.save(user);

        Optional<User> found = userRepository.findByLogin("findme");

        assertTrue(found.isPresent());
        assertEquals("findme", found.get().getLogin());
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional, если пользователь не найден")
    void shouldReturnEmptyWhenUserNotFound() {

        Optional<User> found = userRepository.findByLogin("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Должен проверить существование пользователя по логину")
    void shouldCheckUserExistsByLogin() {

        User user = new User();
        user.setLogin("exists");
        user.setPasswordHash("hashed-password");
        userRepository.save(user);

        assertTrue(userRepository.existsByLogin("exists"));
        assertFalse(userRepository.existsByLogin("notexists"));
    }
}
