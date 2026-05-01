package com.example.netotask52cloudstorageproject.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.ResponseEntity;
import com.example.netotask52cloudstorageproject.dto.LoginRequest;
import com.example.netotask52cloudstorageproject.dto.LoginResponse;
import com.example.netotask52cloudstorageproject.service.AuthService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cloud")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request.getLogin(), request.getPassword());
            LoginResponse response = new LoginResponse(token);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // 🔹 Возвращаем ошибки в формате, который ждёт фронтенд
            Map<String, List<String>> errors = new HashMap<>();
            errors.put("email", List.of(e.getMessage()));
            errors.put("password", List.of());

            return ResponseEntity.badRequest().body(errors);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("auth-token") String token) {
        try {
            authService.logout(token);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).build();
        }
    }
}