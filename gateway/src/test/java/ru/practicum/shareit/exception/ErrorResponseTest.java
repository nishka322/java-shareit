package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.ErrorResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ErrorResponseTest {

    @Test
    void testConstructorAndGetters() {
        ErrorResponse response = new ErrorResponse("Ошибка", "Описание");
        assertThat(response.getError()).isEqualTo("Ошибка");
        assertThat(response.getDescription()).isEqualTo("Описание");
    }

    @Test
    void testConstructorWithNullDescription() {
        ErrorResponse response = new ErrorResponse("Ошибка", null);
        assertThat(response.getError()).isEqualTo("Ошибка");
        assertThat(response.getDescription()).isNull();
    }

    @Test
    void testConstructorThrowsWhenErrorIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ErrorResponse(null, "Описание")
        );
        assertThat(exception.getMessage()).contains("cannot be null");
    }

    @Test
    void testConstructorThrowsWhenErrorIsEmpty() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ErrorResponse("", "Описание")
        );
        assertThat(exception.getMessage()).contains("cannot be null");
    }

    @Test
    void testToString() {
        ErrorResponse response = new ErrorResponse("Ошибка", "Описание");
        String result = response.toString();
        assertThat(result).contains("Ошибка");
        assertThat(result).contains("Описание");
    }
}