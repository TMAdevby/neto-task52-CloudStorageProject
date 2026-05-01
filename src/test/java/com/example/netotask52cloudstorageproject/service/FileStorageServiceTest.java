package com.example.netotask52cloudstorageproject.service;

import com.example.netotask52cloudstorageproject.dto.FileInfo;
import com.example.netotask52cloudstorageproject.model.StoredFile;
import com.example.netotask52cloudstorageproject.model.User;
import com.example.netotask52cloudstorageproject.repository.FileRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@DisplayName("FileStorageService Тесты")
class FileStorageServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Когда загружается файл")
    class FileUpload {

        @Test
        @DisplayName("Должен сохранить файл при успешной загрузке")
        void shouldSaveFileOnSuccessfulUpload() throws IOException {

            User testUser = new User();
            testUser.setId(1L);
            testUser.setLogin("testuser");

            when(multipartFile.isEmpty()).thenReturn(false);
            when(multipartFile.getOriginalFilename()).thenReturn("test.txt");
            when(multipartFile.getSize()).thenReturn(1024L);
            when(multipartFile.getContentType()).thenReturn("text/plain");
            when(multipartFile.getBytes()).thenReturn("test content".getBytes());

            when(fileRepository.existsByUserAndFilename(testUser, "test.txt")).thenReturn(false);

            StoredFile savedFile = new StoredFile();
            savedFile.setId(1L);
            savedFile.setFilename("test.txt");
            savedFile.setSize(1024L);
            when(fileRepository.save(any(StoredFile.class))).thenReturn(savedFile);

            StoredFile result = fileStorageService.uploadFile(testUser, "test.txt", multipartFile);

            assertNotNull(result);
            verify(fileRepository, times(1)).save(any(StoredFile.class));
        }

        @Test
        @DisplayName("Должен выбросить исключение, если файл пустой")
        void shouldThrowExceptionWhenFileIsEmpty() {

            User testUser = new User();

            when(multipartFile.isEmpty()).thenReturn(true);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> fileStorageService.uploadFile(testUser, "test.txt", multipartFile)
            );

            assertEquals("Файл не может быть пустым", exception.getMessage());
        }

        @Test
        @DisplayName("Должен выбросить исключение, если файл с таким именем уже существует")
        void shouldThrowExceptionWhenFileAlreadyExists() {

            User testUser = new User();

            when(multipartFile.isEmpty()).thenReturn(false);
            when(fileRepository.existsByUserAndFilename(testUser, "test.txt")).thenReturn(true);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> fileStorageService.uploadFile(testUser, "test.txt", multipartFile)
            );

            assertEquals("Файл с таким именем уже существует", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Когда получается список файлов")
    class FileList {

        @Test
        @DisplayName("Должен вернуть список файлов пользователя")
        void shouldReturnUserFileList() {

            User testUser = new User();
            testUser.setId(1L);

            StoredFile file1 = new StoredFile();
            file1.setId(1L);
            file1.setFilename("file1.txt");
            file1.setSize(100L);

            StoredFile file2 = new StoredFile();
            file2.setId(2L);
            file2.setFilename("file2.txt");
            file2.setSize(200L);

            List<StoredFile> mockFiles = Arrays.asList(file1, file2);
            when(fileRepository.findByUserOrderByUploadedAtDesc(testUser)).thenReturn(mockFiles);

            List<FileInfo> result = fileStorageService.getFileList(testUser, null);

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("file1.txt", result.get(0).getFilename());
            assertEquals("file2.txt", result.get(1).getFilename());
        }

        @Test
        @DisplayName("Должен применить лимит к списку файлов")
        void shouldApplyLimitToFileList() {

            User testUser = new User();

            StoredFile file1 = new StoredFile();
            file1.setFilename("file1.txt");
            file1.setSize(100L);

            StoredFile file2 = new StoredFile();
            file2.setFilename("file2.txt");
            file2.setSize(200L);

            List<StoredFile> mockFiles = Arrays.asList(file1, file2);
            when(fileRepository.findByUserOrderByUploadedAtDesc(testUser)).thenReturn(mockFiles);

            List<FileInfo> result = fileStorageService.getFileList(testUser, 1);

            assertEquals(1, result.size(), "Должен вернуться только 1 файл по лимиту");
        }
    }
}
