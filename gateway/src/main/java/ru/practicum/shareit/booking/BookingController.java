package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exceptions.UnknownStateException;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {

        BookingState state = BookingState.from(stateParam.toUpperCase())
                .orElseThrow(() -> new UnknownStateException("Unknown state: " + stateParam));

        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                   @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {

        BookingState state = BookingState.from(stateParam.toUpperCase())
                .orElseThrow(() -> new UnknownStateException("Unknown state: " + stateParam));

        log.info("Get owner bookings with state {}, userId={}, from={}, size={}",
                stateParam, userId, from, size);

        return bookingClient.getOwnerBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookingRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam Boolean approved) {
        log.info("Update booking {}, userId={}, approved={}", bookingId, userId, approved);
        return bookingClient.updateBooking(userId, bookingId, approved);
    }
}