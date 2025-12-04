package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Qualifier("inMemoryRepo")
@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> itemsRepository = new HashMap<>();
    private long maxId = 0;

    @Override
    public Item createItem(long userId, Item item) {
        item.setId(generateId());
        item.setOwnerId(userId);
        itemsRepository.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> getAllUserItems(long userId) {
        return itemsRepository.values().stream().filter(i -> i.getOwnerId() == userId).toList();
    }

    @Override
    public Item getItemById(long itemId) {
        return itemsRepository.get(itemId);
    }

    @Override
    public Item editItem(Item item, long itemId) {
        itemsRepository.put(itemId, item);
        return item;
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        String lowText = text.toLowerCase();
        return itemsRepository.values().stream().filter(i -> (i.getName().toLowerCase().contains(lowText) ||
                i.getDescription().toLowerCase().contains(lowText)) && i.isAvailable()).toList();
    }

    private long generateId() {
        return ++maxId;
    }
}