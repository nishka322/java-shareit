package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentAndBookingDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ItemMapperImpl.class})
class ItemMapperTest {

    @Autowired
    private ItemMapper mapper;

    @Test
    void shouldMapItemToItemDto() {
        ItemRequest request = new ItemRequest();
        request.setId(10L);

        User owner = new User();
        owner.setId(1L);

        Item item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .request(request)
                .build();

        ItemDto dto = mapper.mapToDto(item);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Item", dto.getName());
        assertEquals("Description", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(10L, dto.getRequestId());
    }

    @Test
    void shouldMapItemToItemDtoWhenRequestIsNull() {
        User owner = new User();
        owner.setId(1L);

        Item item = Item.builder()
                .id(1L)
                .name("Item")
                .available(true)
                .request(null)
                .build();

        ItemDto dto = mapper.mapToDto(item);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertNull(dto.getRequestId());
    }

    @Test
    void shouldMapItemDtoToItem() {
        ItemDto dto = ItemDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .requestId(10L)
                .build();

        Item item = mapper.mapToItem(dto);

        assertNotNull(item);
        assertEquals("Item", item.getName());
        assertEquals("Description", item.getDescription());
        assertTrue(item.isAvailable());
    }

    @Test
    void shouldMapItemsToItemDtos() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Item1")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Item2")
                .available(false)
                .build();

        List<ItemDto> dtos = mapper.mapItemsToItemDtos(List.of(item1, item2));

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals("Item1", dtos.get(0).getName());
        assertEquals("Item2", dtos.get(1).getName());
    }

    @Test
    void shouldUpdateItemWhenAllFieldsProvided() {
        Item item = Item.builder()
                .name("Old")
                .description("Old desc")
                .available(true)
                .build();

        ItemDto dto = ItemDto.builder()
                .name("New")
                .description("New desc")
                .available(false)
                .build();

        Item updated = mapper.updateItem(item, dto);

        assertEquals("New", updated.getName());
        assertEquals("New desc", updated.getDescription());
        assertFalse(updated.isAvailable());
    }

    @Test
    void shouldUpdateItemWhenOnlyNameProvided() {
        Item item = Item.builder()
                .name("Old")
                .description("Old desc")
                .available(true)
                .build();

        ItemDto dto = ItemDto.builder()
                .name("New")
                .build();

        Item updated = mapper.updateItem(item, dto);

        assertEquals("New", updated.getName());
        assertEquals("Old desc", updated.getDescription());
        assertTrue(updated.isAvailable());
    }

    @Test
    void shouldUpdateItemWhenOnlyDescriptionProvided() {
        Item item = Item.builder()
                .name("Old")
                .description("Old desc")
                .available(true)
                .build();

        ItemDto dto = ItemDto.builder()
                .description("New desc")
                .build();

        Item updated = mapper.updateItem(item, dto);

        assertEquals("Old", updated.getName());
        assertEquals("New desc", updated.getDescription());
        assertTrue(updated.isAvailable());
    }

    @Test
    void shouldUpdateItemWhenOnlyAvailableProvided() {
        Item item = Item.builder()
                .name("Old")
                .description("Old desc")
                .available(true)
                .build();

        ItemDto dto = ItemDto.builder()
                .available(false)
                .build();

        Item updated = mapper.updateItem(item, dto);

        assertEquals("Old", updated.getName());
        assertEquals("Old desc", updated.getDescription());
        assertFalse(updated.isAvailable());
    }

    @Test
    void shouldNotUpdateItemWhenEmptyName() {
        Item item = Item.builder()
                .name("Old")
                .description("Old desc")
                .available(true)
                .build();

        ItemDto dto = ItemDto.builder()
                .name(" ")
                .build();

        Item updated = mapper.updateItem(item, dto);

        assertEquals("Old", updated.getName());
        assertEquals("Old desc", updated.getDescription());
        assertTrue(updated.isAvailable());
    }

    @Test
    void shouldNotUpdateItemWhenEmptyDescription() {
        Item item = Item.builder()
                .name("Old")
                .description("Old desc")
                .available(true)
                .build();

        ItemDto dto = ItemDto.builder()
                .description(" ")
                .build();

        Item updated = mapper.updateItem(item, dto);

        assertEquals("Old", updated.getName());
        assertEquals("Old desc", updated.getDescription());
        assertTrue(updated.isAvailable());
    }

    @Test
    void shouldMapItemToItemWithBookingAndComments() {
        Item item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        BookingDateDto lastBooking = BookingDateDto.builder()
                .id(10L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now())
                .build();

        BookingDateDto nextBooking = BookingDateDto.builder()
                .id(20L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        CommentResponseDto comment1 = CommentResponseDto.builder()
                .id(100L)
                .text("Great item!")
                .authorName("User1")
                .created(LocalDateTime.now())
                .build();

        CommentResponseDto comment2 = CommentResponseDto.builder()
                .id(200L)
                .text("Good quality")
                .authorName("User2")
                .created(LocalDateTime.now())
                .build();

        List<CommentResponseDto> comments = List.of(comment1, comment2);

        ItemWithCommentAndBookingDto dto = mapper.mapItemToItemWithBooking(
                item, lastBooking, nextBooking, comments
        );

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Item", dto.getName());
        assertEquals("Description", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(lastBooking, dto.getLastBooking());
        assertEquals(nextBooking, dto.getNextBooking());
        assertEquals(2, dto.getComments().size());
        assertEquals("Great item!", dto.getComments().get(0).getText());
        assertEquals("Good quality", dto.getComments().get(1).getText());
    }

    @Test
    void shouldMapItemToItemWithBookingWhenNoBookings() {
        Item item = Item.builder()
                .id(1L)
                .name("Item")
                .available(true)
                .build();

        ItemWithCommentAndBookingDto dto = mapper.mapItemToItemWithBooking(
                item, null, null, List.of()
        );

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Item", dto.getName());
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
        assertTrue(dto.getComments().isEmpty());
    }

    // null

    @Test
    void shouldReturnNullWhenMapToDtoWithNullItem() {
        ItemDto result = mapper.mapToDto(null);
        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenMapToItemWithNullDto() {
        Item result = mapper.mapToItem(null);
        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenMapItemsToItemDtosWithNullList() {
        List<ItemDto> result = mapper.mapItemsToItemDtos(null);
        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenMapItemToItemWithBookingWithAllNulls() {
        ItemWithCommentAndBookingDto result = mapper.mapItemToItemWithBooking(
                null, null, null, null
        );
        assertNull(result);
    }

    @Test
    void shouldMapItemToItemWithBookingWhenOnlyItemProvided() {
        Item item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        ItemWithCommentAndBookingDto result = mapper.mapItemToItemWithBooking(
                item, null, null, null
        );

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Item", result.getName());
        assertEquals("Description", result.getDescription());
        assertTrue(result.getAvailable());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertNull(result.getComments());
    }

    @Test
    void itemRequestIdShouldReturnNullWhenItemIsNull() {
        ItemDto dto = mapper.mapToDto(null);
        assertNull(dto);
    }

    @Test
    void itemRequestIdShouldReturnNullWhenRequestIsNull() {
        Item item = Item.builder()
                .id(1L)
                .name("Item")
                .request(null)
                .build();

        ItemDto dto = mapper.mapToDto(item);
        assertNotNull(dto);
        assertNull(dto.getRequestId());
    }

    @Test
    void itemRequestIdShouldReturnNullWhenRequestIdIsNull() {
        ItemRequest request = new ItemRequest();
        request.setId(null);

        Item item = Item.builder()
                .id(1L)
                .name("Item")
                .request(request)
                .build();

        ItemDto dto = mapper.mapToDto(item);
        assertNotNull(dto);
        assertNull(dto.getRequestId());
    }

    @Test
    void shouldUpdateItemWhenDtoIsNull() {
        Item item = Item.builder()
                .name("Old")
                .description("Old desc")
                .available(true)
                .build();

        ItemDto emptyDto = ItemDto.builder().build();
        Item updated = mapper.updateItem(item, emptyDto);

        assertEquals("Old", updated.getName());
        assertEquals("Old desc", updated.getDescription());
        assertTrue(updated.isAvailable());
    }

    @Test
    void shouldUpdateItemWhenDtoHasNullFields() {
        Item item = Item.builder()
                .name("Old")
                .description("Old desc")
                .available(true)
                .build();

        ItemDto dto = ItemDto.builder().build();

        Item updated = mapper.updateItem(item, dto);

        assertEquals("Old", updated.getName());
        assertEquals("Old desc", updated.getDescription());
        assertTrue(updated.isAvailable());
    }
}