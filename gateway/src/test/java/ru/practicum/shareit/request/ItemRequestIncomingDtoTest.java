package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ru.practicum.shareit.request.dto.ItemRequestIncomingDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestIncomingDtoTest {

    private final Validator validator;

    public ItemRequestIncomingDtoTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldCreateValidItemRequestIncomingDto() {
        ItemRequestIncomingDto dto = new ItemRequestIncomingDto();
        dto.setDescription("Need a drill for construction work");

        Set<ConstraintViolation<ItemRequestIncomingDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Не должно быть нарушений валидации");
    }

    @Test
    void shouldFailWhenDescriptionIsBlank() {
        ItemRequestIncomingDto dto = new ItemRequestIncomingDto();
        dto.setDescription("");

        Set<ConstraintViolation<ItemRequestIncomingDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Должно быть нарушение валидации");
        assertEquals(1, violations.size());
        assertEquals("description", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenDescriptionIsNull() {
        ItemRequestIncomingDto dto = new ItemRequestIncomingDto();
        dto.setDescription(null);

        Set<ConstraintViolation<ItemRequestIncomingDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Должно быть нарушение валидации");
        assertEquals(1, violations.size());
        assertEquals("description", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldHaveAllArgsConstructor() {
        String description = "Need a drill";
        ItemRequestIncomingDto dto = new ItemRequestIncomingDto(description);

        assertEquals(description, dto.getDescription());
    }

    @Test
    void shouldHaveNoArgsConstructor() {
        ItemRequestIncomingDto dto = new ItemRequestIncomingDto();
        assertNull(dto.getDescription());

        dto.setDescription("Test");
        assertEquals("Test", dto.getDescription());
    }
}