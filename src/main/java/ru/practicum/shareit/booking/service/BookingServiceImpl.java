package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.WrongRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper mapper;
    private final UserMapper userMapper;

    @Override
    public BookingResponseDto makeBooking(BookingRequestDto dto, long userId) {
        UserDto userDto = userService.getUserById(userId);

        Item item = itemService.getItemEntityById(dto.getItemId());

        if (!item.isAvailable()) {
            throw new WrongRequestException("Объект не доступен для бронирования.");
        }

        if (item.getOwnerId() == userId) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        if (dto.getStart().isAfter(dto.getEnd())) {
            throw new WrongRequestException("Дата начала должна быть раньше даты окончания");
        }

        if (dto.getStart().isBefore(LocalDateTime.now())) {
            throw new WrongRequestException("Дата начала не может быть в прошлом");
        }

        Booking booking = mapper.mapRequestDtoToBooking(dto);
        booking.setBooker(userMapper.toEntity(userDto));
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return mapper.mapBookingToResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto approveBooking(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            throw new NotFoundException("Бронирование не найдено");
        }

        Item item = booking.getItem();

        if (userId != item.getOwnerId()) {
            throw new WrongRequestException("Пользователь не является собственником и не может подтверждать бронь");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new WrongRequestException("Бронирование уже было обработано");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return mapper.mapBookingToResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            throw new NotFoundException("Бронирование не найдено");
        }

        if (booking.getBooker().getId() != userId && booking.getItem().getOwnerId() != userId) {
            throw new NotFoundException("У пользователя нет прав доступа к информации о бронировании.");
        }

        return mapper.mapBookingToResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllUserBooking(long userId, BookingState state) {
        userService.getUserById(userId);

        List<Booking> bookings = bookingRepository.findByBookerId(userId);
        return filterBookingsByState(bookings, state);
    }

    @Override
    public List<BookingResponseDto> getAllUserItemBooking(long userId, BookingState state) {
        userService.getUserById(userId);

        List<Booking> bookings = bookingRepository.findAllByOwnerId(userId);
        return filterBookingsByState(bookings, state);
    }

    private List<BookingResponseDto> filterBookingsByState(List<Booking> bookings, BookingState state) {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> filteredBookings = switch (state) {
            case PAST -> bookings.stream()
                    .filter(booking -> booking.getEnd().isBefore(now))
                    .collect(Collectors.toList());

            case FUTURE -> bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .collect(Collectors.toList());

            case CURRENT -> bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                    .collect(Collectors.toList());

            case WAITING -> bookings.stream()
                    .filter(booking -> booking.getStatus() == BookingStatus.WAITING)
                    .collect(Collectors.toList());

            case REJECTED -> bookings.stream()
                    .filter(booking -> booking.getStatus() == BookingStatus.REJECTED)
                    .collect(Collectors.toList());

            default -> bookings;
        };

        return filteredBookings.stream()
                .map(mapper::mapBookingToResponseDto)
                .collect(Collectors.toList());
    }
}