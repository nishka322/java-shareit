package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item saveItem(Item item);

    List<Item> findByOwnerId(long userId);

    Optional<Item> findById(Long id);

    Item editItem(Item item);

    List<Item> searchByText(String text);
}