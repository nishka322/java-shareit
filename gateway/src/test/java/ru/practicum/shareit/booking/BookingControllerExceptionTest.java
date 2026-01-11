package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerExceptionTest {

    @Mock
    private BookingClient bookingClient;

    @Test
    void shouldHandleClientErrors() {
        BookingController controller = new BookingController(bookingClient);

        when(bookingClient.getBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.badRequest().build());

        ResponseEntity<Object> response = controller.getBookings(1L, "ALL", 0, 10);

        assertNotNull(response);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void shouldHandleInvalidDates() {
        BookingController controller = new BookingController(bookingClient);
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(1L);
        requestDto.setStart(LocalDateTime.now().plusDays(2));
        requestDto.setEnd(LocalDateTime.now().plusDays(1));

        ResponseEntity<Object> response = controller.bookItem(1L, requestDto);

        verify(bookingClient).bookItem(eq(1L), eq(requestDto));
    }
}