package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ItemRequestMapperImpl.class})
class ItemRequestMapperTest {

    @Autowired
    private ItemRequestMapper mapper;

    @Test
    void shouldMapItemRequestToDto() {
        User requestor = new User();
        requestor.setId(1L);

        ItemRequest itemRequest = ItemRequest.builder()
                .id(10L)
                .description("Need a drill")
                .requestor(requestor)
                .created(LocalDateTime.of(2024, 1, 15, 10, 30, 0))
                .build();

        ItemRequestDto dto = mapper.toDto(itemRequest);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("Need a drill", dto.getDescription());
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30, 0), dto.getCreated());
        assertNull(dto.getItems());
    }

    @Test
    void shouldMapDtoToEntityIgnoringIdAndRelations() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(10L)
                .description("Need a hammer")
                .created(LocalDateTime.now())
                .build();

        ItemRequest entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals("Need a hammer", entity.getDescription());
        assertNull(entity.getRequestor());
        assertNull(entity.getCreated());
        assertNull(entity.getItems());
    }

    @Test
    void shouldHandleNullInput() {
        assertNull(mapper.toDto(null));
        assertNull(mapper.toEntity(null));
    }
}