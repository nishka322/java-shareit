package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ru.practicum.shareit.item.dto.ItemWithCommentAndBookingDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemWithCommentAndBookingDtoTest {

    private final Validator validator;

    public ItemWithCommentAndBookingDtoTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldCreateValidItemWithCommentAndBookingDto() {
        CommentResponseDto comment = CommentResponseDto.builder()
                .id(1L)
                .text("Great!")
                .authorName("John")
                .created(LocalDateTime.now())
                .build();

        ItemWithCommentAndBookingDto dto = ItemWithCommentAndBookingDto.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .comments(List.of(comment))
                .build();

        Set<ConstraintViolation<ItemWithCommentAndBookingDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Не должно быть нарушений валидации");
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        ItemWithCommentAndBookingDto dto = ItemWithCommentAndBookingDto.builder()
                .id(1L)
                .name("")
                .description("Powerful drill")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemWithCommentAndBookingDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Должно быть нарушение валидации");
        assertEquals(1, violations.size());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenDescriptionIsBlank() {
        ItemWithCommentAndBookingDto dto = ItemWithCommentAndBookingDto.builder()
                .id(1L)
                .name("Drill")
                .description("")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemWithCommentAndBookingDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Должно быть нарушение валидации");
        assertEquals(1, violations.size());
        assertEquals("description", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenAvailableIsNull() {
        ItemWithCommentAndBookingDto dto = ItemWithCommentAndBookingDto.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(null)
                .build();

        Set<ConstraintViolation<ItemWithCommentAndBookingDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Должно быть нарушение валидации");
        assertEquals(1, violations.size());
        assertEquals("available", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldAllowEmptyCommentsList() {
        ItemWithCommentAndBookingDto dto = ItemWithCommentAndBookingDto.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .comments(List.of())
                .build();

        Set<ConstraintViolation<ItemWithCommentAndBookingDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Не должно быть нарушений валидации");
    }
}