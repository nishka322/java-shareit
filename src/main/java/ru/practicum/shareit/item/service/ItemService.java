package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.comment.NewCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentAndBookingDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto getItemDtoById(long itemId);

    ItemDto editItem(long userId, ItemDto itemDto, long itemId);

    List<ItemDto> search(String text);

    CommentResponseDto addComment(Long userId, long itemId, NewCommentDto dto);

    List<ItemWithCommentAndBookingDto> getAllUserItems(long userId);

    ItemWithCommentAndBookingDto getItemWithCommentById(long userId, long itemId);

    Item getItemEntityById(Long itemId);
}