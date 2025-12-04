package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@RequestHeader(Constants.USER_ID_HEADER) Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Post new item. Item is {}, owner id is {}", itemDto.getName(), userId);
        return itemService.createItem(userId, itemDto);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("Getting all items for user with id = {}", userId);
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.info("Getting information about item with id = {}", itemId);
        return itemService.getItemById(itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@RequestHeader(Constants.USER_ID_HEADER) Long userId, @RequestBody ItemDto itemDto, @PathVariable long itemId) {
        log.info("Edit {}, owner id is {}", itemDto.getName(), userId);
        return itemService.editItem(userId, itemDto, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String text) {
        log.info("Getting items by request: {}", text);
        return itemService.search(text);
    }


}