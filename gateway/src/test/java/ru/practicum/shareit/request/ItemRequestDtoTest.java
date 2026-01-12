package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestDtoTest {

    private final Validator validator;

    public ItemRequestDtoTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldCreateValidItemRequestDto() {
        ItemRequestDto.ItemResponseDto itemResponse = ItemRequestDto.ItemResponseDto.builder()
                .id(1L)
                .name("Drill")
                .ownerId(2L)
                .description("Powerful drill")
                .available(true)
                .requestId(3L)
                .build();

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill for construction work")
                .created(LocalDateTime.now())
                .items(List.of(itemResponse))
                .build();

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Не должно быть нарушений валидации");
    }

    @Test
    void shouldFailWhenDescriptionIsBlank() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("")
                .created(LocalDateTime.now())
                .build();

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Должно быть нарушение валидации");
        assertEquals(1, violations.size());
        assertEquals("description", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldAllowNullDescriptionForResponseDto() {
        ItemRequestDto.ItemResponseDto itemResponse = ItemRequestDto.ItemResponseDto.builder()
                .id(1L)
                .name("Drill")
                .ownerId(2L)
                .description(null)
                .available(true)
                .requestId(3L)
                .build();

        Set<ConstraintViolation<ItemRequestDto.ItemResponseDto>> violations =
                validator.validate(itemResponse);
        assertTrue(violations.isEmpty(), "Null description должно быть допустимо");
    }

    @Test
    void shouldCreateItemResponseDtoWithAllFields() {
        ItemRequestDto.ItemResponseDto itemResponse = ItemRequestDto.ItemResponseDto.builder()
                .id(1L)
                .name("Drill")
                .ownerId(2L)
                .description("Powerful drill for home use")
                .available(true)
                .requestId(3L)
                .build();

        assertEquals(1L, itemResponse.getId());
        assertEquals("Drill", itemResponse.getName());
        assertEquals(2L, itemResponse.getOwnerId());
        assertEquals("Powerful drill for home use", itemResponse.getDescription());
        assertTrue(itemResponse.getAvailable());
        assertEquals(3L, itemResponse.getRequestId());
    }
}