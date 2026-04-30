
package com.example.netotask52cloudstorageproject.exception;

import com.example.netotask52cloudstorageproject.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(

            IllegalArgumentException ex
    ) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), 400);
        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(java.io.FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileNotFoundException(
            java.io.FileNotFoundException ex
    ) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), 404);
        return ResponseEntity
                .notFound()
                .build();
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(

            MaxUploadSizeExceededException ex
    ) {
        ErrorResponse error = new ErrorResponse(
                "Размер файла превышает максимально допустимый",
                400
        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex
    ) {
        System.err.println("Необработанная ошибка: " + ex.getMessage());
        ex.printStackTrace();

        ErrorResponse error = new ErrorResponse(
                "Внутренняя ошибка сервера",
                500
        );

        return ResponseEntity
                .internalServerError()
                .body(error);
    }
}
