
package com.example.netotask52cloudstorageproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor

public class ErrorResponse {

    private String message;
    private Integer id;

    public ErrorResponse(String message, Integer id) {
        this.message = message;
        this.id = id;
    }

    public static ErrorResponse badRequest(String message) {
        return new ErrorResponse(message, 400);
    }

    public static ErrorResponse unauthorized(String message) {
        return new ErrorResponse(message, 401);
    }

    public static ErrorResponse internalError(String message) {
        return new ErrorResponse(message, 500);
    }
}