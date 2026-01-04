package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentAndBookingDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.comment.NewCommentDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemServiceImpl itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@RequestHeader(USER_ID_HEADER) Long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        log.info("Post new item. Item is {}, owner id is {}", itemDto.getName(), userId);
        return itemService.createItem(userId, itemDto);
    }

    @GetMapping
    public List<ItemWithCommentAndBookingDto> getAllUserItems(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Getting all items for user with id = {}", userId);
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithCommentAndBookingDto getItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @PathVariable Long itemId) {
        log.info("Getting information about item with id = {} for user {}", itemId, userId);
        return itemService.getItemWithCommentById(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@RequestHeader(USER_ID_HEADER) Long userId,
                            @RequestBody ItemDto itemDto,
                            @PathVariable long itemId) {
        log.info("Edit item with id {}, owner id is {}", itemId, userId);
        itemDto.setId(itemId);
        return itemService.editItem(userId, itemDto, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String text) {
        log.info("Getting items by request: {}", text);
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @PathVariable Long itemId,
                                         @Valid @RequestBody NewCommentDto commentDto) {
        log.info("Adding comment to item {} by user {}", itemId, userId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}