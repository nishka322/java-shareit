package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentAndBookingDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemDto mapToDto(Item item);

    List<ItemDto> mapItemsToItemDtos(List<Item> items);

    Item mapToItem(ItemDto dto);


    @Mapping(target = "lastBooking", source = "lastBooking")
    @Mapping(target = "nextBooking", source = "nextBooking")
    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "comments", source = "comments")
    ItemWithCommentAndBookingDto mapItemToItemWithBooking(Item item, BookingDateDto lastBooking, BookingDateDto nextBooking, List<CommentResponseDto> comments);


    default Item updateItem(Item item, ItemDto dto) {
        if (dto.getName() != null && !dto.getName().isBlank()) {
            item.setName(dto.getName());
        }

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            item.setDescription(dto.getDescription());
        }

        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
        return item;
    }

}