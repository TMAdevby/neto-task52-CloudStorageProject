package com.example.netotask52cloudstorageproject.repository;

import com.example.netotask52cloudstorageproject.model.StoredFile;
import com.example.netotask52cloudstorageproject.model.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

@Testcontainers
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("FileRepository Интеграционные тесты")
class FileRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private FileRepository fileRepository;

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
        fileRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Должен сохранить файл в БД")
    void shouldSaveFileToDatabase() {
        User user = createTestUser();

        StoredFile file = new StoredFile();
        file.setUser(user);
        file.setFilename("test.txt");
        file.setStorageFilename("uuid_test.txt");
        file.setSize(1024L);
        file.setContentType("text/plain");

        StoredFile savedFile = fileRepository.save(file);

        assertNotNull(savedFile.getId());
        assertEquals("test.txt", savedFile.getFilename());
        assertEquals(1024L, savedFile.getSize());
    }

    @Test
    @DisplayName("Должен найти файлы пользователя")
    void shouldFindFilesByUser() {
        User user = createTestUser();

        StoredFile file1 = createTestFile(user, "file1.txt", 100L);
        StoredFile file2 = createTestFile(user, "file2.txt", 200L);

        fileRepository.saveAll(List.of(file1, file2));

        List<StoredFile> files = fileRepository.findByUserOrderByUploadedAtDesc(user);

        assertEquals(2, files.size());
    }

    @Test
    @DisplayName("Должен найти файл пользователя по имени")
    void shouldFindFileByUserAndFilename() {
        User user = createTestUser();

        StoredFile file = createTestFile(user, "unique.txt", 500L);
        fileRepository.save(file);

        Optional<StoredFile> found = fileRepository.findByUserAndFilename(user, "unique.txt");

        assertTrue(found.isPresent());
        assertEquals("unique.txt", found.get().getFilename());
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional, если файл не найден")
    void shouldReturnEmptyWhenFileNotFound() {
        User user = createTestUser();

        Optional<StoredFile> found = fileRepository.findByUserAndFilename(user, "nonexistent.txt");

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Должен проверить существование файла у пользователя")
    void shouldCheckFileExistsByUserAndFilename() {
        User user = createTestUser();

        StoredFile file = createTestFile(user, "exists.txt", 100L);
        fileRepository.save(file);

        assertTrue(fileRepository.existsByUserAndFilename(user, "exists.txt"));
        assertFalse(fileRepository.existsByUserAndFilename(user, "notexists.txt"));
    }

    @Test
    @DisplayName("Должен удалить файл пользователя по имени")
    void shouldDeleteFileByUserAndFilename() {
        User user = createTestUser();

        StoredFile file = createTestFile(user, "todelete.txt", 100L);
        fileRepository.save(file);

        fileRepository.deleteByUserAndFilename(user, "todelete.txt");

        Optional<StoredFile> found = fileRepository.findByUserAndFilename(user, "todelete.txt");
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Должен найти файл по хэшу")
    void shouldFindFileByHash() {
        User user = createTestUser();

        StoredFile file = createTestFile(user, "hashfile.txt", 100L);
        file.setFileHash("abc123def456");
        fileRepository.save(file);

        Optional<StoredFile> found = fileRepository.findByFileHash("abc123def456");

        assertTrue(found.isPresent());
        assertEquals("abc123def456", found.get().getFileHash());
    }

    private User createTestUser() {
        User user = new User();
        user.setLogin("testuser-" + System.currentTimeMillis());
        user.setPasswordHash("hashed-password");
        return userRepository.save(user);
    }

    private StoredFile createTestFile(User user, String filename, Long size) {
        StoredFile file = new StoredFile();
        file.setUser(user);
        file.setFilename(filename);
        file.setStorageFilename("uuid_" + filename);
        file.setSize(size);
        file.setContentType("text/plain");
        return file;
    }
}
