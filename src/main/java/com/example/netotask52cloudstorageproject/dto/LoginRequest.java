package com.example.netotask52cloudstorageproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor

public class LoginRequest {

    @JsonProperty("login")
    private String login;

    @JsonProperty("password")
    private String password;

    public LoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }
}