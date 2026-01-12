package ru.practicum.shareit.booking.dto;

import java.util.Optional;
import java.util.stream.Stream;

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static Optional<BookingState> from(String state) {
        return Stream.of(values())
                .filter(bookingState -> bookingState.name().equalsIgnoreCase(state))
                .findFirst();
    }
}