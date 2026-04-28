
package com.example.netotask52cloudstorageproject.service;


import com.example.netotask52cloudstorageproject.model.AuthToken;
import com.example.netotask52cloudstorageproject.model.User;
import com.example.netotask52cloudstorageproject.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import java.time.LocalDateTime;

@Service
public class TokenService {

    private final Map<String, AuthToken> tokenStore = new ConcurrentHashMap<>();

    private static final long TOKEN_VALIDITY_HOURS = 24;

    private final UserRepository userRepository;

    @Autowired
    public TokenService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(User user) {

        String token = UUID.randomUUID().toString().replace("-", "");

        LocalDateTime expiresAt = LocalDateTime.now().plusHours(TOKEN_VALIDITY_HOURS);

        AuthToken authToken = new AuthToken(token, user, expiresAt);

        tokenStore.put(token, authToken);

        return token;
    }

    public Optional<User> validateToken(String token) {

        AuthToken authToken = tokenStore.get(token);

        if (authToken == null) {
            return Optional.empty();
        }

        if (!authToken.isValid()) {
            return Optional.empty();
        }

        return Optional.of(authToken.getUser());
    }

    public void invalidateToken(String token) {

        AuthToken authToken = tokenStore.get(token);

        if (authToken != null) {
            authToken.invalidate();
        }
    }

    public Optional<AuthToken> getToken(String token) {
        return Optional.ofNullable(tokenStore.get(token));
    }
}