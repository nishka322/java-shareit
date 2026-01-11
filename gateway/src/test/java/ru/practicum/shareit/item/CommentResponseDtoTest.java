package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentResponseDtoTest {

    private final Validator validator;

    public CommentResponseDtoTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldCreateValidCommentResponseDto() {
        CommentResponseDto dto = CommentResponseDto.builder()
                .id(1L)
                .text("Great item!")
                .authorName("John Doe")
                .created(LocalDateTime.now())
                .build();

        Set<ConstraintViolation<CommentResponseDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Не должно быть нарушений валидации");
    }

    @Test
    void shouldFailWhenTextIsBlank() {
        CommentResponseDto dto = CommentResponseDto.builder()
                .id(1L)
                .text("")
                .authorName("John Doe")
                .created(LocalDateTime.now())
                .build();

        Set<ConstraintViolation<CommentResponseDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Должно быть нарушение валидации");
        assertEquals(1, violations.size());
        assertEquals("text", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenAuthorNameIsBlank() {
        CommentResponseDto dto = CommentResponseDto.builder()
                .id(1L)
                .text("Great item!")
                .authorName("")
                .created(LocalDateTime.now())
                .build();

        Set<ConstraintViolation<CommentResponseDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Должно быть нарушение валидации");
        assertEquals(1, violations.size());
        assertEquals("authorName", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenCreatedIsNull() {
        CommentResponseDto dto = CommentResponseDto.builder()
                .id(1L)
                .text("Great item!")
                .authorName("John Doe")
                .created(null)
                .build();

        Set<ConstraintViolation<CommentResponseDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Должно быть нарушение валидации");
        assertEquals(1, violations.size());
        assertEquals("created", violations.iterator().next().getPropertyPath().toString());
    }
}