package com.example.taskapp.web.error;

import com.example.taskapp.service.NotFoundException;
import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(HttpStatus.NOT_FOUND, ex.getMessage(), req));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, WebRequest req) {
        Map<String,String> details = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a,b)->a));
        return ResponseEntity.badRequest().body(ApiError.of(HttpStatus.BAD_REQUEST, "Validazione fallita", req, details));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req));
    }

    @Value @Builder
    static class ApiError {
        int status; String error; String message; String path;
        OffsetDateTime timestamp; Map<String,String> details;

        static ApiError of(HttpStatus s, String msg, WebRequest req) { return of(s, msg, req, null); }
        static ApiError of(HttpStatus s, String msg, WebRequest req, Map<String,String> details) {
            return ApiError.builder()
                    .status(s.value()).error(s.getReasonPhrase()).message(msg)
                    .path(req.getDescription(false).replace("uri=",""))
                    .timestamp(OffsetDateTime.now())
                    .details(details)
                    .build();
        }
    }
}