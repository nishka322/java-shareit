package ru.practicum.shareit.request;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncomingDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User requestor;
    private User otherUser;

    @BeforeEach
    void setUp() {
        requestor = userRepository.save(
                User.builder()
                        .name("Requestor")
                        .email("requestor@email.com")
                        .build()
        );

        otherUser = userRepository.save(
                User.builder()
                        .name("Other User")
                        .email("other@email.com")
                        .build()
        );
    }

    @Test
    void shouldCreateRequestAndPersistInDatabase() {
        ItemRequestIncomingDto dto = new ItemRequestIncomingDto("Need a power drill");

        ItemRequestDto created = itemRequestService.createRequest(requestor.getId(), dto);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Need a power drill", created.getDescription());
        assertNotNull(created.getCreated());
        assertNull(created.getItems());

        ItemRequest persisted = itemRequestRepository.findById(created.getId()).orElse(null);
        assertNotNull(persisted);
        assertEquals(created.getDescription(), persisted.getDescription());
        assertEquals(requestor.getId(), persisted.getRequestor().getId());
    }

    @Test
    void shouldReturnOwnRequests() {
        ItemRequestIncomingDto dto1 = new ItemRequestIncomingDto("Need a drill");
        itemRequestService.createRequest(requestor.getId(), dto1);

        ItemRequestIncomingDto dto2 = new ItemRequestIncomingDto("Need a hammer");
        itemRequestService.createRequest(requestor.getId(), dto2);

        List<ItemRequestDto> ownRequests = itemRequestService.getOwnRequests(requestor.getId());

        assertNotNull(ownRequests);
        assertEquals(2, ownRequests.size());
        assertEquals("Need a hammer", ownRequests.get(0).getDescription());
        assertEquals("Need a drill", ownRequests.get(1).getDescription());
    }

    @Test
    void shouldReturnEmptyListWhenNoOwnRequests() {
        List<ItemRequestDto> ownRequests = itemRequestService.getOwnRequests(requestor.getId());

        assertNotNull(ownRequests);
        assertTrue(ownRequests.isEmpty());
    }

    @Test
    void shouldReturnAllRequestsFromOtherUsers() {
        ItemRequestIncomingDto requestorRequest = new ItemRequestIncomingDto("Requestor's request");
        itemRequestService.createRequest(requestor.getId(), requestorRequest);

        ItemRequestIncomingDto otherUserRequest = new ItemRequestIncomingDto("Other user's request");
        itemRequestService.createRequest(otherUser.getId(), otherUserRequest);

        List<ItemRequestDto> allRequestsForRequestor = itemRequestService.getAllRequests(requestor.getId());

        assertNotNull(allRequestsForRequestor);
        assertEquals(1, allRequestsForRequestor.size());
        assertEquals("Other user's request", allRequestsForRequestor.getFirst().getDescription());
    }

    @Test
    void shouldReturnEmptyListWhenNoOtherUsersRequests() {
        List<ItemRequestDto> allRequests = itemRequestService.getAllRequests(requestor.getId());

        assertNotNull(allRequests);
        assertTrue(allRequests.isEmpty());
    }

    @Test
    void shouldReturnRequestById() {
        ItemRequestIncomingDto dto = new ItemRequestIncomingDto("Specific request");
        ItemRequestDto created = itemRequestService.createRequest(requestor.getId(), dto);

        ItemRequestDto found = itemRequestService.getRequestById(requestor.getId(), created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Specific request", found.getDescription());
        assertNotNull(found.getCreated());
    }

    @Test
    void shouldReturnRequestWithItemsWhenItemsExist() {
        ItemRequestIncomingDto dto = new ItemRequestIncomingDto("Request with items");
        ItemRequestDto created = itemRequestService.createRequest(requestor.getId(), dto);

        ItemRequestDto found = itemRequestService.getRequestById(requestor.getId(), created.getId());

        assertNotNull(found);
        assertNotNull(found.getItems());
        assertTrue(found.getItems().isEmpty());
    }
}