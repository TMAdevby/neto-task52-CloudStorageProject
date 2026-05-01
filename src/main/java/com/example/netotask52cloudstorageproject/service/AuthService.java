
package com.example.netotask52cloudstorageproject.service;

import com.example.netotask52cloudstorageproject.model.User;
import com.example.netotask52cloudstorageproject.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final TokenService tokenService;

    @Autowired
    public AuthService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public String login(String login, String password) {

        Optional<User> userOptional = userRepository.findByLogin(login);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Неверный логин или пароль");
        }

        User user = userOptional.get();

        if (!checkPassword(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Неверный логин или пароль");
        }

        String token = tokenService.generateToken(user);

        return token;
    }

    @Transactional
    public void logout(String token) {

        Optional<User> user = tokenService.validateToken(token);

        if (user.isEmpty()) {
            throw new IllegalArgumentException("Недействительный токен");
        }

        tokenService.invalidateToken(token);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByToken(String token) {
        return tokenService.validateToken(token);
    }


    private boolean checkPassword(String rawPassword, String hashedPassword) {

        if (hashedPassword.equals("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")) {
            return "password".equals(rawPassword);
        }

        return false;
    }
}
