package com.example.invoice.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private String message;
    private int status;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, String> errors;
}
