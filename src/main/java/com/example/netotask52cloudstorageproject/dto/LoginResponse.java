
package com.example.netotask52cloudstorageproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor

public class LoginResponse {

    @JsonProperty("auth-token")
    private String authToken;

    public LoginResponse(String authToken) {
        this.authToken = authToken;
    }
}
