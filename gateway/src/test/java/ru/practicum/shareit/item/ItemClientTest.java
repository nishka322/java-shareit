package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.dto.ItemDto;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<HttpEntity<?>> httpEntityCaptor;

    @Captor
    private ArgumentCaptor<Map<String, Object>> mapCaptor;

    private ItemClient itemClient;

    @BeforeEach
    void setUp() {
        itemClient = new ItemClient("http://localhost:8080", new RestTemplateBuilder());

        try {
            Field restField = itemClient.getClass().getSuperclass().getDeclaredField("rest");
            restField.setAccessible(true);
            restField.set(itemClient, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set restTemplate field", e);
        }
    }

    @Test
    void shouldCreateItem() {
        long userId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(""),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.createItem(userId, itemDto);

        assertNotNull(response);

        verify(restTemplate).exchange(
                eq(""),
                eq(HttpMethod.POST),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        HttpHeaders headers = capturedEntity.getHeaders();
        assertNotNull(headers);
        assertEquals("1", headers.getFirst("X-Sharer-User-Id"));
        assertEquals(itemDto, capturedEntity.getBody());
    }

    @Test
    void shouldUpdateItem() {
        long userId = 1L;
        long itemId = 100L;
        ItemDto itemDto = ItemDto.builder()
                .name("Updated Drill")
                .description("More powerful")
                .available(false)
                .build();

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq("/100"),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.updateItem(userId, itemId, itemDto);

        assertNotNull(response);

        verify(restTemplate).exchange(
                eq("/100"),
                eq(HttpMethod.PATCH),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        assertEquals("1", capturedEntity.getHeaders().getFirst("X-Sharer-User-Id"));
        assertEquals(itemDto, capturedEntity.getBody());
    }

    @Test
    void shouldGetItem() {
        long userId = 1L;
        long itemId = 100L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq("/100"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.getItem(userId, itemId);

        assertNotNull(response);

        verify(restTemplate).exchange(
                eq("/100"),
                eq(HttpMethod.GET),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        assertEquals("1", capturedEntity.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void shouldGetAllUserItems() {
        long userId = 1L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(""),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.getAllUserItems(userId);

        assertNotNull(response);

        verify(restTemplate).exchange(
                eq(""),
                eq(HttpMethod.GET),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        assertEquals("1", capturedEntity.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void shouldSearchItems() {
        String text = "drill";
        Integer from = 0;
        Integer size = 10;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq("/search?text={text}&from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                any(Map.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.searchItems(text, from, size);

        assertNotNull(response);

        verify(restTemplate).exchange(
                eq("/search?text={text}&from={from}&size={size}"),
                eq(HttpMethod.GET),
                httpEntityCaptor.capture(),
                eq(Object.class),
                mapCaptor.capture()
        );

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        assertNull(capturedEntity.getHeaders().getFirst("X-Sharer-User-Id"));

        Map<String, Object> capturedMap = mapCaptor.getValue();
        assertEquals("drill", capturedMap.get("text"));
        assertEquals(0, capturedMap.get("from"));
        assertEquals(10, capturedMap.get("size"));
    }

    @Test
    void shouldAddComment() {
        long userId = 1L;
        long itemId = 100L;
        Object commentDto = "Test comment";

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq("/100/comment"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.addComment(userId, itemId, commentDto);

        assertNotNull(response);

        verify(restTemplate).exchange(
                eq("/100/comment"),
                eq(HttpMethod.POST),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        assertEquals("1", capturedEntity.getHeaders().getFirst("X-Sharer-User-Id"));
        assertEquals(commentDto, capturedEntity.getBody());
    }
}