package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto makeBooking(BookingRequestDto dto, long userId);

    BookingResponseDto approveBooking(long userId, long bookingId, boolean approved);

    BookingResponseDto getBookingById(long userId, long bookingId);

    List<BookingResponseDto> getAllUserBooking(long userId, BookingState state);

    List<BookingResponseDto> getAllUserItemBooking(long userId, BookingState state);
}