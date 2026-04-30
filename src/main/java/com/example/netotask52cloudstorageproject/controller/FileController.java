
package com.example.netotask52cloudstorageproject.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.example.netotask52cloudstorageproject.dto.FileInfo;
import com.example.netotask52cloudstorageproject.dto.ErrorResponse;

import com.example.netotask52cloudstorageproject.service.FileStorageService;

import com.example.netotask52cloudstorageproject.model.User;

import lombok.RequiredArgsConstructor;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/cloud")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadFile(

            @RequestHeader("auth-token") String token,

            @RequestParam("filename") String filename,

            @RequestParam("file") MultipartFile file
    ) {

        try {
            fileStorageService.uploadFile(getUserByToken(token), filename, file);

            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .build();
        } catch (IOException e) {

            return ResponseEntity
                    .internalServerError()  // HTTP 500
                    .build();
        }
    }

    @GetMapping("/file")
    @ResponseBody
    public ResponseEntity<byte[]> downloadFile(

            @RequestHeader("auth-token") String token,

            @RequestParam("filename") String filename
    ) {

        try {
            byte[] fileContent = fileStorageService.downloadFile(getUserByToken(token), filename);

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")  // 🔹 Подсказка браузеру
                    .body(fileContent);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .notFound()
                    .build();
        } catch (IOException e) {
            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }

    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(

            @RequestHeader("auth-token") String token,

            @RequestParam("filename") String filename
    ) {

        try {
            fileStorageService.deleteFile(getUserByToken(token), filename);

            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .notFound()
                    .build();
        } catch (IOException e) {
            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }

    @PutMapping("/file")
    public ResponseEntity<Void> renameFile(

            @RequestHeader("auth-token") String token,

            @RequestParam("filename") String currentFilename,

            @RequestBody RenameRequest request
    ) {

        try {
            fileStorageService.renameFile(
                    getUserByToken(token),
                    currentFilename,
                    request.getName()
            );

            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileInfo>> getFileList(

            @RequestHeader("auth-token") String token,

            @RequestParam(value = "limit", required = false) Integer limit
    ) {

        try {
            List<FileInfo> files = fileStorageService.getFileList(getUserByToken(token), limit);

            return ResponseEntity.ok(files);

        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }

    private User getUserByToken(String token) {
        User user = new User();
        user.setId(1L);
        user.setLogin("testuser");
        return user;
    }
}

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class RenameRequest {
    private String name;
}
