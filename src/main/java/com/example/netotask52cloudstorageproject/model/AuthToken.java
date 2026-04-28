
package com.example.netotask52cloudstorageproject.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthToken {

    private String token;
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean active;

    public AuthToken(String token, User user, LocalDateTime expiresAt) {
        this.token = token;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.active = true;
    }

    public boolean isValid() {
        return this.active && LocalDateTime.now().isBefore(this.expiresAt);
    }

    public void invalidate() {
        this.active = false;
    }
}
