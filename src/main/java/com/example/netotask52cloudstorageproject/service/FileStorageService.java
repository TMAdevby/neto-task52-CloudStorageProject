
package com.example.netotask52cloudstorageproject.service;

import com.example.netotask52cloudstorageproject.model.StoredFile;
import com.example.netotask52cloudstorageproject.model.User;
import com.example.netotask52cloudstorageproject.dto.FileInfo;
import com.example.netotask52cloudstorageproject.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class FileStorageService {

    private final FileRepository fileRepository;

    private static final String STORAGE_PATH = "uploads/";

    @Autowired
    public FileStorageService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;

        createStorageDirectory();
    }

    private void createStorageDirectory() {
        try {
            Files.createDirectories(Paths.get(STORAGE_PATH));
        } catch (IOException e) {
            System.err.println("Не удалось создать папку для файлов: " + STORAGE_PATH);
            throw new RuntimeException("Ошибка инициализации хранилища", e);
        }
    }

    @Transactional
    public StoredFile uploadFile(User user, String filename, MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл не может быть пустым");
        }

        if (fileRepository.existsByUserAndFilename(user, filename)) {
            throw new IllegalArgumentException("Файл с таким именем уже существует");
        }

        String storageFilename = UUID.randomUUID() + "_" + filename;

        String fileHash = calculateFileHash(file);

        Path filePath = Paths.get(STORAGE_PATH).resolve(storageFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        StoredFile storedFile = new StoredFile();
        storedFile.setUser(user);
        storedFile.setFilename(filename);
        storedFile.setStorageFilename(storageFilename);
        storedFile.setContentType(file.getContentType());
        storedFile.setSize(file.getSize());
        storedFile.setFileHash(fileHash);

        return fileRepository.save(storedFile);
    }

    @Transactional(readOnly = true)
    public byte[] downloadFile(User user, String filename) throws IOException {

        StoredFile file = fileRepository.findByUserAndFilename(user, filename)
                .orElseThrow(() -> new IllegalArgumentException("Файл не найден"));

        Path filePath = Paths.get(STORAGE_PATH).resolve(file.getStorageFilename());
        return Files.readAllBytes(filePath);
    }

    @Transactional
    public void deleteFile(User user, String filename) throws IOException {

        StoredFile file = fileRepository.findByUserAndFilename(user, filename)
                .orElseThrow(() -> new IllegalArgumentException("Файл не найден"));

        Path filePath = Paths.get(STORAGE_PATH).resolve(file.getStorageFilename());
        Files.deleteIfExists(filePath);

        fileRepository.delete(file);
    }

    @Transactional
    public StoredFile renameFile(User user, String oldFilename, String newFilename) {

        if (fileRepository.existsByUserAndFilename(user, newFilename)) {
            throw new IllegalArgumentException("Файл с таким именем уже существует");
        }

        StoredFile file = fileRepository.findByUserAndFilename(user, oldFilename)
                .orElseThrow(() -> new IllegalArgumentException("Файл не найден"));

        file.setFilename(newFilename);

        return fileRepository.save(file);
    }

    @Transactional(readOnly = true)
    public List<FileInfo> getFileList(User user, Integer limit) {

        List<StoredFile> files = fileRepository.findByUserOrderByUploadedAtDesc(user);

        if (limit != null && limit > 0 && limit < files.size()) {
            files = files.subList(0, limit);
        }

        return files.stream()
                .map(FileInfo::fromEntity)
                .collect(Collectors.toList());
    }

    private String calculateFileHash(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(bytes);

            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();

        } catch (IOException | NoSuchAlgorithmException e) {
            return "";
        }
    }

    public Path getFilePath(String storageFilename) {
        return Paths.get(STORAGE_PATH).resolve(storageFilename);
    }
}
