package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practicum.shareit.booking.dto.BookingState;

import static org.junit.jupiter.api.Assertions.*;

class BookingStateTest {

    @Test
    void shouldContainAllStates() {
        BookingState[] states = BookingState.values();
        assertEquals(6, states.length);
        assertArrayEquals(
                new BookingState[]{BookingState.ALL, BookingState.CURRENT,
                        BookingState.PAST, BookingState.FUTURE,
                        BookingState.WAITING, BookingState.REJECTED},
                states
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"})
    void shouldParseValidState(String stateName) {
        BookingState state = BookingState.valueOf(stateName);
        assertNotNull(state);
        assertEquals(stateName, state.name());
    }

    @Test
    void shouldHandleCaseInsensitiveInController() {
        String stateParam = "all";
        BookingState state = BookingState.valueOf(stateParam.toUpperCase());
        assertEquals(BookingState.ALL, state);
    }
}