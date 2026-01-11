package ru.practicum.shareit.exceptions;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ErrorResponse {
    private final String error;
    private final String description;

    public ErrorResponse(String error, String description) {
        if (error == null || error.isEmpty()) {
            throw new IllegalArgumentException("Error message cannot be null or empty");
        }
        this.error = error;
        this.description = description;
    }
}