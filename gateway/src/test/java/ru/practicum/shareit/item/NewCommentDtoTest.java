package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ru.practicum.shareit.item.dto.comment.NewCommentDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NewCommentDtoTest {

    private final Validator validator;

    public NewCommentDtoTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldCreateValidNewCommentDto() {
        NewCommentDto dto = NewCommentDto.builder()
                .text("Great item! Very useful.")
                .build();

        Set<ConstraintViolation<NewCommentDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Не должно быть нарушений валидации");
    }

    @Test
    void shouldFailWhenTextIsBlank() {
        NewCommentDto dto = NewCommentDto.builder()
                .text("")
                .build();

        Set<ConstraintViolation<NewCommentDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Должно быть нарушение валидации");
        assertEquals(2, violations.size());
        assertEquals("text", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenTextIsTooShort() {
        NewCommentDto dto = NewCommentDto.builder()
                .text("")
                .build();

        Set<ConstraintViolation<NewCommentDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Должно быть нарушение валидации");
    }

    @Test
    void shouldFailWhenTextIsTooLong() {
        String longText = "a".repeat(501);
        NewCommentDto dto = NewCommentDto.builder()
                .text(longText)
                .build();

        Set<ConstraintViolation<NewCommentDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Должно быть нарушение валидации");
        assertEquals(1, violations.size());
        assertEquals("text", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldAcceptMaxLengthText() {
        String maxLengthText = "a".repeat(500);
        NewCommentDto dto = NewCommentDto.builder()
                .text(maxLengthText)
                .build();

        Set<ConstraintViolation<NewCommentDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Не должно быть нарушений валидации");
    }
}