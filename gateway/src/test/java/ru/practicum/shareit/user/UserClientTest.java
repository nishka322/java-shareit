package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<HttpEntity<?>> httpEntityCaptor;

    private UserClient userClient;

    @BeforeEach
    void setUp() {
        userClient = new UserClient("http://localhost:8080", new RestTemplateBuilder());

        try {
            Field restField = userClient.getClass().getSuperclass().getDeclaredField("rest");
            restField.setAccessible(true);
            restField.set(userClient, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set restTemplate field", e);
        }
    }

    @Test
    void shouldCreateUser() {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(""),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.createUser(userDto);

        assertNotNull(response);

        verify(restTemplate).exchange(
                eq(""),
                eq(HttpMethod.POST),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        assertNull(capturedEntity.getHeaders().getFirst("X-Sharer-User-Id"));
        assertEquals(userDto, capturedEntity.getBody());
    }

    @Test
    void shouldUpdateUser() {
        long userId = 1L;
        UserDto userDto = UserDto.builder()
                .name("John Updated")
                .email("john.updated@example.com")
                .build();

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq("/1"),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.updateUser(userId, userDto);

        assertNotNull(response);

        verify(restTemplate).exchange(
                eq("/1"),
                eq(HttpMethod.PATCH),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        assertNull(capturedEntity.getHeaders().getFirst("X-Sharer-User-Id"));
        assertEquals(userDto, capturedEntity.getBody());
    }

    @Test
    void shouldGetUser() {
        long userId = 1L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq("/1"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.getUser(userId);

        assertNotNull(response);

        verify(restTemplate).exchange(
                eq("/1"),
                eq(HttpMethod.GET),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        assertNull(capturedEntity.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void shouldGetAllUsers() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(""),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.getAllUsers();

        assertNotNull(response);

        verify(restTemplate).exchange(
                eq(""),
                eq(HttpMethod.GET),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        assertNull(capturedEntity.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void shouldDeleteUser() {
        long userId = 1L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq("/1"),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.deleteUser(userId);

        assertNotNull(response);

        verify(restTemplate).exchange(
                eq("/1"),
                eq(HttpMethod.DELETE),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        assertNull(capturedEntity.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void shouldHandleEmptyUserDto() {
        UserDto userDto = UserDto.builder().build();

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                eq(""),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.createUser(userDto);

        assertNotNull(response);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class));
    }
}