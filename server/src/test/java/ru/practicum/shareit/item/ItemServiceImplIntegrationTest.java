package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentAndBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User testUser;
    private ItemDto testItemDto;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(
                User.builder()
                        .name("Test User")
                        .email("test.user@email.com")
                        .build()
        );

        testItemDto = ItemDto.builder()
                .name("Power Drill")
                .description("Professional power drill for home use")
                .available(true)
                .build();
    }

    @Test
    void shouldCreateAndPersistItem() {
        ItemDto createdItem = itemService.createItem(testUser.getId(), testItemDto);

        assertNotNull(createdItem);
        assertNotNull(createdItem.getId());
        assertEquals("Power Drill", createdItem.getName());
        assertEquals("Professional power drill for home use", createdItem.getDescription());
        assertTrue(createdItem.getAvailable());

        Item persistedItem = itemRepository.findById(createdItem.getId()).orElse(null);
        assertNotNull(persistedItem);
        assertEquals(createdItem.getName(), persistedItem.getName());
        assertEquals(createdItem.getDescription(), persistedItem.getDescription());
        assertEquals(createdItem.getAvailable(), persistedItem.isAvailable());
    }

    @Test
    void shouldUpdateExistingItem() {
        ItemDto createdItem = itemService.createItem(testUser.getId(), testItemDto);

        ItemDto updateDto = ItemDto.builder()
                .name("Updated Drill")
                .description("Updated description")
                .available(false)
                .build();

        ItemDto updatedItem = itemService.editItem(testUser.getId(), updateDto, createdItem.getId());

        assertNotNull(updatedItem);
        assertEquals(createdItem.getId(), updatedItem.getId());
        assertEquals("Updated Drill", updatedItem.getName());
        assertEquals("Updated description", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
    }

    @Test
    void shouldReturnAllUserItems() {
        itemService.createItem(testUser.getId(), testItemDto);

        ItemDto anotherItem = ItemDto.builder()
                .name("Hammer")
                .description("Heavy duty hammer")
                .available(true)
                .build();
        itemService.createItem(testUser.getId(), anotherItem);

        List<ItemWithCommentAndBookingDto> userItems = itemService.getAllUserItems(testUser.getId());

        assertNotNull(userItems);
        assertEquals(2, userItems.size());
        assertTrue(userItems.stream().anyMatch(item -> item.getName().equals("Power Drill")));
        assertTrue(userItems.stream().anyMatch(item -> item.getName().equals("Hammer")));
    }

    @Test
    void shouldSearchItemsByText() {
        ItemDto createdItem = itemService.createItem(testUser.getId(), testItemDto);

        List<ItemDto> searchResults = itemService.search("drill");

        assertNotNull(searchResults);
        assertEquals(1, searchResults.size());
        assertEquals(createdItem.getId(), searchResults.get(0).getId());

        List<ItemDto> searchResultsByDescription = itemService.search("professional");

        assertEquals(1, searchResultsByDescription.size());
        assertEquals(createdItem.getId(), searchResultsByDescription.get(0).getId());
    }

    @Test
    void shouldNotReturnUnavailableItemsInSearch() {
        ItemDto availableItem = ItemDto.builder()
                .name("Power Drill")
                .description("Good drill")
                .available(true)
                .build();
        itemService.createItem(testUser.getId(), availableItem);

        ItemDto unavailableItem = ItemDto.builder()
                .name("Broken Drill")
                .description("Not working")
                .available(false)
                .build();
        itemService.createItem(testUser.getId(), unavailableItem);

        List<ItemDto> searchResults = itemService.search("drill");

        assertEquals(1, searchResults.size());
        assertEquals("Power Drill", searchResults.get(0).getName());
    }

    @Test
    void shouldReturnEmptyListWhenSearchTextIsBlank() {
        itemService.createItem(testUser.getId(), testItemDto);

        List<ItemDto> searchResults = itemService.search("");

        assertNotNull(searchResults);
        assertTrue(searchResults.isEmpty());
    }

    @Test
    void shouldSearchCaseInsensitive() {
        ItemDto createdItem = itemService.createItem(testUser.getId(), testItemDto);

        List<ItemDto> searchResults1 = itemService.search("DRILL");
        List<ItemDto> searchResults2 = itemService.search("DrIlL");
        List<ItemDto> searchResults3 = itemService.search("power");

        assertEquals(1, searchResults1.size());
        assertEquals(1, searchResults2.size());
        assertEquals(1, searchResults3.size());
        assertEquals(createdItem.getId(), searchResults1.get(0).getId());
        assertEquals(createdItem.getId(), searchResults2.get(0).getId());
        assertEquals(createdItem.getId(), searchResults3.get(0).getId());
    }
}