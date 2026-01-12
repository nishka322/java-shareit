package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exceptions.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    void handleValidation() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("object", "field", "defaultMessage");
        when(ex.getFieldError()).thenReturn(fieldError);

        ErrorResponse response = exceptionHandler.handleValidation(ex);

        assertThat(response.getError()).isEqualTo("Not valid.");
        assertThat(response.getDescription()).isEqualTo("defaultMessage");
    }

    @Test
    void handleAlreadyExists() {
        AlreadyExistsException ex = new AlreadyExistsException("Пользователь уже существует");

        ErrorResponse response = exceptionHandler.handleAlreadyExists(ex);

        assertThat(response.getError()).isEqualTo("Resource already exist.");
        assertThat(response.getDescription()).isEqualTo("Пользователь уже существует");
    }

    @Test
    void handleNotFound() {
        NotFoundException ex = new NotFoundException("Пользователь не найден");

        ErrorResponse response = exceptionHandler.handleNotFound(ex);

        assertThat(response.getError()).isEqualTo("Candidates not found.");
        assertThat(response.getDescription()).isEqualTo("Пользователь не найден");
    }

    @Test
    void handleWrongRequest() {
        WrongRequestException ex = new WrongRequestException("Неверные даты");

        ErrorResponse response = exceptionHandler.handleWrongRequest(ex);

        assertThat(response.getError()).isEqualTo("Wrong request data.");
        assertThat(response.getDescription()).isEqualTo("Неверные даты");
    }

    @Test
    void handleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Неверный аргумент");

        ErrorResponse response = exceptionHandler.handleIllegalArgument(ex);

        assertThat(response.getError()).isEqualTo("Wrong request data.");
        assertThat(response.getDescription()).isEqualTo("Неверный аргумент");
    }
}