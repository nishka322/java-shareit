package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository {
    Item createItem(User owner, Item item);

    List<Item> getAllUserItems(long userId);

    Item getItemById(long itemId);

    Item editItem(Item item, long itemId);

    List<Item> search(String text);
}