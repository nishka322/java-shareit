package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestTest {

    @Test
    void testNoArgsConstructor() {
        ItemRequest request = new ItemRequest();

        assertThat(request).isNotNull();
        assertThat(request.getId()).isNull();
        assertThat(request.getDescription()).isNull();
        assertThat(request.getRequestor()).isNull();
        assertThat(request.getCreated()).isNull();
        assertThat(request.getItems()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setId(1L);

        ItemRequest request = new ItemRequest(1L, "Нужна дрель", user, now);

        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getDescription()).isEqualTo("Нужна дрель");
        assertThat(request.getRequestor()).isEqualTo(user);
        assertThat(request.getCreated()).isEqualTo(now);
    }

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setId(1L);

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("Нужен молоток")
                .requestor(user)
                .created(now)
                .items(List.of())
                .build();

        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getDescription()).isEqualTo("Нужен молоток");
        assertThat(request.getRequestor()).isEqualTo(user);
        assertThat(request.getCreated()).isEqualTo(now);
        assertThat(request.getItems()).isEmpty();
    }

    @Test
    void testSettersAndGetters() {
        ItemRequest request = new ItemRequest();
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setId(1L);

        request.setId(1L);
        request.setDescription("Нужна пила");
        request.setRequestor(user);
        request.setCreated(now);
        request.setItems(List.of());

        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getDescription()).isEqualTo("Нужна пила");
        assertThat(request.getRequestor()).isEqualTo(user);
        assertThat(request.getCreated()).isEqualTo(now);
        assertThat(request.getItems()).isEmpty();
    }
}