package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestIncomingDto;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<HttpEntity<?>> httpEntityCaptor;

    @Captor
    private ArgumentCaptor<Map<String, Object>> mapCaptor;

    private ItemRequestClient itemRequestClient;

    @BeforeEach
    void setUp() {
        itemRequestClient = new ItemRequestClient("http://localhost:8080", new RestTemplateBuilder());

        try {
            Field restField = itemRequestClient.getClass().getSuperclass().getDeclaredField("rest");
            restField.setAccessible(true);
            restField.set(itemRequestClient, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set restTemplate field", e);
        }
    }

    @Test
    void shouldCreateRequest() {
        long userId = 1L;
        ItemRequestIncomingDto requestDto = new ItemRequestIncomingDto();
        requestDto.setDescription("Need a drill for construction work");

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
            eq(""),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestClient.createRequest(userId, requestDto);

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
        assertEquals(requestDto, capturedEntity.getBody());
    }

    @Test
    void shouldGetOwnRequests() {
        long userId = 1L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
            eq(""),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestClient.getOwnRequests(userId);

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
    void shouldGetAllRequests() {
        long userId = 1L;
        Integer from = 0;
        Integer size = 10;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
            eq("/all?from={from}&size={size}"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Object.class),
            any(Map.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestClient.getAllRequests(userId, from, size);

        assertNotNull(response);

        verify(restTemplate).exchange(
            eq("/all?from={from}&size={size}"),
            eq(HttpMethod.GET),
            httpEntityCaptor.capture(),
            eq(Object.class),
            mapCaptor.capture()
        );

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        assertEquals("1", capturedEntity.getHeaders().getFirst("X-Sharer-User-Id"));

        Map<String, Object> capturedMap = mapCaptor.getValue();
        assertEquals(0, capturedMap.get("from"));
        assertEquals(10, capturedMap.get("size"));
    }

    @Test
    void shouldGetAllRequestsWithCustomParams() {
        long userId = 1L;
        Integer from = 5;
        Integer size = 20;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
            eq("/all?from={from}&size={size}"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Object.class),
            any(Map.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestClient.getAllRequests(userId, from, size);

        assertNotNull(response);

        verify(restTemplate).exchange(
            eq("/all?from={from}&size={size}"),
            eq(HttpMethod.GET),
            httpEntityCaptor.capture(),
            eq(Object.class),
            mapCaptor.capture()
        );

        Map<String, Object> capturedMap = mapCaptor.getValue();
        assertEquals(5, capturedMap.get("from"));
        assertEquals(20, capturedMap.get("size"));
    }

    @Test
    void shouldGetRequestById() {
        long userId = 1L;
        long requestId = 100L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
            eq("/100"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestClient.getRequestById(userId, requestId);

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
}