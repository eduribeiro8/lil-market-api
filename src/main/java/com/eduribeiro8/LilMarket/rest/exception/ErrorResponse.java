package com.eduribeiro8.LilMarket.rest.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<ValidationError> fieldErrors
) {
    public record ValidationError(String field, String message) {}
}
