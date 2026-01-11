package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.AlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongRequestException;

import static org.assertj.core.api.Assertions.assertThat;

class CustomExceptionsTest {

    @Test
    void alreadyExistsException() {
        AlreadyExistsException ex = new AlreadyExistsException("Сообщение");
        assertThat(ex.getMessage()).isEqualTo("Сообщение");
    }

    @Test
    void notFoundException() {
        NotFoundException ex = new NotFoundException("Сообщение");
        assertThat(ex.getMessage()).isEqualTo("Сообщение");
    }

    @Test
    void wrongRequestException() {
        WrongRequestException ex = new WrongRequestException("Сообщение");
        assertThat(ex.getMessage()).isEqualTo("Сообщение");
    }

    @Test
    void validationException() {
        ValidationException ex = new ValidationException("Сообщение");
        assertThat(ex.getMessage()).isEqualTo("Сообщение");
    }
}