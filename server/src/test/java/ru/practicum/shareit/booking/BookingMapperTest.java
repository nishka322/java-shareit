package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingMapperTest {

    @Autowired
    private BookingMapper bookingMapper;

    @Test
    void shouldMapBookingDtoToBooking() {
        Item item = new Item();
        item.setId(1L);

        User booker = new User();
        booker.setId(2L);

        BookingDto dto = BookingDto.builder()
                .id(1L)
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build();

        Booking booking = bookingMapper.mapBookingDtoToBooking(dto);

        assertNotNull(booking);
        assertEquals(dto.getId(), booking.getId());
        assertEquals(dto.getItem().getId(), booking.getItem().getId());
        assertEquals(dto.getBooker().getId(), booking.getBooker().getId());
        assertEquals(dto.getStatus(), booking.getStatus());
    }

    @Test
    void shouldMapRequestDtoToBooking() {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now().plusDays(1));

        Booking booking = bookingMapper.mapRequestDtoToBooking(dto);

        assertNotNull(booking);
        assertEquals(dto.getItemId(), booking.getItem().getId());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
    }

    @Test
    void shouldMapBookingToBookingDto() {
        Item item = new Item();
        item.setId(1L);

        User booker = new User();
        booker.setId(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);

        BookingDto dto = bookingMapper.mapBookingToBookingDto(booking);

        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getItem().getId(), dto.getItem().getId());
        assertEquals(booking.getBooker().getId(), dto.getBooker().getId());
        assertEquals(booking.getStatus(), dto.getStatus());
    }

    @Test
    void shouldMapBookingToResponseDto() {
        Item item = new Item();
        item.setId(1L);

        User booker = new User();
        booker.setId(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);

        BookingResponseDto dto = bookingMapper.mapBookingToResponseDto(booking);

        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getItem().getId(), dto.getItem().getId());
        assertEquals(booking.getBooker().getId(), dto.getBooker().getId());
        assertEquals(booking.getStatus(), dto.getStatus());
    }

    @Test
    void shouldMapBookingToDateDto() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));

        BookingDateDto dto = bookingMapper.mapBookingToDateDto(booking);

        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
    }

    // null

    @Test
    void shouldReturnNullWhenMapBookingDtoToBookingWithNullDto() {
        Booking result = bookingMapper.mapBookingDtoToBooking(null);
        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenMapRequestDtoToBookingWithNullDto() {
        Booking result = bookingMapper.mapRequestDtoToBooking(null);
        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenMapBookingToBookingDtoWithNullBooking() {
        BookingDto result = bookingMapper.mapBookingToBookingDto(null);
        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenMapBookingToResponseDtoWithNullBooking() {
        BookingResponseDto result = bookingMapper.mapBookingToResponseDto(null);
        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenMapBookingToDateDtoWithNullBooking() {
        BookingDateDto result = bookingMapper.mapBookingToDateDto(null);
        assertNull(result);
    }

    @Test
    void shouldMapBookingDtoToBookingWhenFieldsAreNull() {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .item(null)
                .booker(null)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(null)
                .build();

        Booking booking = bookingMapper.mapBookingDtoToBooking(dto);

        assertNotNull(booking);
        assertEquals(1L, booking.getId());
        assertNull(booking.getItem());
        assertNull(booking.getBooker());
        assertNull(booking.getStatus());
    }

    @Test
    void shouldMapBookingToBookingDtoWhenFieldsAreNull() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(null);
        booking.setBooker(null);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(null);

        BookingDto dto = bookingMapper.mapBookingToBookingDto(booking);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertNull(dto.getItem());
        assertNull(dto.getBooker());
        assertNull(dto.getStatus());
    }

    @Test
    void shouldHandleNullItemInItemToItemDto() {
        Booking booking = new Booking();
        booking.setItem(null);

        BookingDto dto = bookingMapper.mapBookingToBookingDto(booking);
        assertNotNull(dto);
        assertNull(dto.getItem());
    }

    @Test
    void shouldHandleNullDatesInBookingToDateDto() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(null);
        booking.setEnd(null);

        BookingDateDto dto = bookingMapper.mapBookingToDateDto(booking);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertNull(dto.getStart());
        assertNull(dto.getEnd());
    }
}