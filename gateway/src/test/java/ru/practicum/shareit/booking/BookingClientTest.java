package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<String> urlCaptor;

    @Captor
    private ArgumentCaptor<HttpMethod> methodCaptor;

    @Captor
    private ArgumentCaptor<Map<String, Object>> parametersCaptor;

    private BookingClient bookingClient;

    @BeforeEach
    void setUp() {
        bookingClient = new BookingClient("http://localhost:8080",
                new org.springframework.boot.web.client.RestTemplateBuilder());

        try {
            var field = bookingClient.getClass().getSuperclass().getDeclaredField("rest");
            field.setAccessible(true);
            field.set(bookingClient, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldGetBookings() {
        long userId = 1L;
        BookingState state = BookingState.ALL;
        Integer from = 0;
        Integer size = 10;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class), any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBookings(userId, state, from, size);

        assertNotNull(response);
        verify(restTemplate).exchange(
                urlCaptor.capture(),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                parametersCaptor.capture()
        );

        String url = urlCaptor.getValue();
        assertTrue(url.contains("?state="));
        assertTrue(url.contains("&from="));
        assertTrue(url.contains("&size="));

        Map<String, Object> params = parametersCaptor.getValue();
        assertEquals("ALL", params.get("state"));
        assertEquals(0, params.get("from"));
        assertEquals(10, params.get("size"));
    }

    @Test
    void shouldGetOwnerBookings() {
        long userId = 1L;
        BookingState state = BookingState.CURRENT;
        Integer from = 5;
        Integer size = 20;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class), any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getOwnerBookings(userId, state, from, size);

        assertNotNull(response);
        verify(restTemplate).exchange(
                argThat(url -> url.contains("/owner")),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                parametersCaptor.capture()
        );

        Map<String, Object> params = parametersCaptor.getValue();
        assertEquals("CURRENT", params.get("state"));
        assertEquals(5, params.get("from"));
        assertEquals(20, params.get("size"));
    }

    @Test
    void shouldBookItem() {
        long userId = 1L;
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(1L);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(""),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.bookItem(userId, requestDto);

        assertNotNull(response);
        verify(restTemplate).exchange(
                eq(""),
                eq(HttpMethod.POST),
                argThat(entity -> {
                    HttpHeaders headers = entity.getHeaders();
                    return headers != null &&
                            "1".equals(headers.getFirst("X-Sharer-User-Id")) &&
                            entity.getBody().equals(requestDto);
                }),
                eq(Object.class)
        );
    }

    @Test
    void shouldGetBooking() {
        long userId = 1L;
        Long bookingId = 100L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(
                eq("/100"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBooking(userId, bookingId);

        assertNotNull(response);
        verify(restTemplate).exchange(
                eq("/100"),
                eq(HttpMethod.GET),
                argThat(entity -> {
                    HttpHeaders headers = entity.getHeaders();
                    return headers != null &&
                            "1".equals(headers.getFirst("X-Sharer-User-Id"));
                }),
                eq(Object.class)
        );
    }

    @Test
    void shouldUpdateBooking() {
        long userId = 1L;
        long bookingId = 100L;
        Boolean approved = true;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(), eq(Object.class), any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.updateBooking(userId, bookingId, approved);

        assertNotNull(response);
        verify(restTemplate).exchange(
                urlCaptor.capture(),
                eq(HttpMethod.PATCH),
                any(),
                eq(Object.class),
                parametersCaptor.capture()
        );

        String url = urlCaptor.getValue();
        assertTrue(url.contains("/100?approved="));

        Map<String, Object> params = parametersCaptor.getValue();
        assertEquals(true, params.get("approved"));
    }
}