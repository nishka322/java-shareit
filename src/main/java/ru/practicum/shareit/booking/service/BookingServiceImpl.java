package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Sort;
import jakarta.validation.Valid;
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
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper mapper;
    private final UserMapper userMapper;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponseDto makeBooking(@Valid BookingRequestDto dto, long userId) {
        UserDto userDto = userService.getUserById(userId);

        Item item = itemService.getItemEntityById(dto.getItemId());

        if (!item.isAvailable()) {
            throw new WrongRequestException("Объект не доступен для бронирования.");
        }

        Booking booking = mapper.mapRequestDtoToBooking(dto);
        booking.setBooker(userMapper.toEntity(userDto));
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return mapper.mapBookingToResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto approveBooking(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
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
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
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

        Sort newestFirst = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBookerId(userId);
            case PAST -> bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), newestFirst);
            case FUTURE -> bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), newestFirst);
            case CURRENT ->
                    bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), newestFirst);
            case WAITING -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, newestFirst);
            case REJECTED -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, newestFirst);
        };

        return bookings.stream()
                .map(mapper::mapBookingToResponseDto)
                .toList();
    }


    @Override
    public List<BookingResponseDto> getAllUserItemBooking(long userId, BookingState state) {
        userService.getUserById(userId);

        Sort newestFirst = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByOwnerId(userId);
            case CURRENT -> bookingRepository.findCurrentByOwner(userId, now, newestFirst);
            case PAST -> bookingRepository.findPastByOwner(userId, now, BookingStatus.APPROVED, newestFirst);
            case FUTURE -> bookingRepository.findFutureByOwner(userId, now, newestFirst);
            case WAITING -> bookingRepository.findByItemInAndStatus(
                    itemRepository.findByOwnerId(userId),
                    BookingStatus.WAITING,
                    newestFirst
            );
            case REJECTED -> bookingRepository.findByItemInAndStatus(
                    itemRepository.findByOwnerId(userId),
                    BookingStatus.REJECTED,
                    newestFirst
            );
        };

        return bookings.stream()
                .map(mapper::mapBookingToResponseDto)
                .toList();
    }
}