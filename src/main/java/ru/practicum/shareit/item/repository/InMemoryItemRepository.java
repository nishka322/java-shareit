package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Qualifier("inMemoryRepo")
@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> itemsRepository = new HashMap<>();
    private long maxId = 0;

    @Override
    public Item saveItem(Item item) {
        item.setId(generateId());
        itemsRepository.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> findByOwnerId(long userId) {
        return itemsRepository.values().stream().filter(i -> i.getOwnerId() == userId).toList();
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.of(itemsRepository.get(id));
    }

    @Override
    public Item editItem(Item item) {
        itemsRepository.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> searchByText(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        String lowText = text.toLowerCase();
        return itemsRepository.values().stream().filter(i -> (i.getName().toLowerCase().contains(lowText) || i.getDescription().toLowerCase().contains(lowText)) && i.isAvailable()).toList();
    }

    private long generateId() {
        return ++maxId;
    }
}