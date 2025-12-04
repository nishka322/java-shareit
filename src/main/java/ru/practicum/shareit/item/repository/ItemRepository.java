package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item createItem(long userId, Item item);

    List<Item> getAllUserItems(long userId);

    Item getItemById(long itemId);

    Item editItem(Item item, long itemId);

    List<Item> search(String text);
}