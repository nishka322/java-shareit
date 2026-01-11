package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.service.BookingState;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.WrongRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private UserService userService;
    @Mock private ItemService itemService;
    @Mock private BookingMapper bookingMapper;
    @Mock private ItemRepository itemRepository;

    @InjectMocks private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).name("Owner").email("owner@email.com").build();
        booker = User.builder().id(2L).name("Booker").email("booker@email.com").build();

        item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .ownerId(1L)
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void makeBookingShouldThrowWhenItemNotAvailable() {
        item.setAvailable(false);
        when(userService.getUserById(2L)).thenReturn(new UserDto());
        when(itemService.getItemEntityById(1L)).thenReturn(item);

        assertThatThrownBy(() -> bookingService.makeBooking(bookingRequestDto, 2L))
                .isInstanceOf(WrongRequestException.class)
                .hasMessageContaining("Объект не доступен для бронирования");
    }

    @Test
    void approveBookingShouldThrowWhenUserNotOwner() {
        item.setOwnerId(999L); // Другой владелец
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approveBooking(1L, 1L, true))
                .isInstanceOf(WrongRequestException.class)
                .hasMessageContaining("Пользователь не является собственником");
    }

    @Test
    void approveBookingShouldThrowWhenStatusNotWaiting() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approveBooking(1L, 1L, true))
                .isInstanceOf(WrongRequestException.class)
                .hasMessageContaining("Бронирование уже было обработано");
    }

    @Test
    void getBookingByIdShouldThrowWhenUserHasNoAccess() {
        booking.setBooker(booker);
        booking.getItem().setOwnerId(owner.getId());

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBookingById(999L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("У пользователя нет прав доступа");
    }

    @Test
    void getAllUserBookingShouldReturnAll() {
        when(userService.getUserById(2L)).thenReturn(new UserDto());
        when(bookingRepository.findByBookerId(2L)).thenReturn(List.of(booking));
        when(bookingMapper.mapBookingToResponseDto(booking)).thenReturn(new BookingResponseDto());

        List<BookingResponseDto> result = bookingService.getAllUserBooking(2L, BookingState.ALL);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByBookerId(2L);
    }

    @Test
    void getAllUserBookingShouldReturnPast() {
        when(userService.getUserById(2L)).thenReturn(new UserDto());
        when(bookingRepository.findByBookerIdAndEndIsBefore(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.mapBookingToResponseDto(booking)).thenReturn(new BookingResponseDto());

        List<BookingResponseDto> result = bookingService.getAllUserBooking(2L, BookingState.PAST);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByBookerIdAndEndIsBefore(anyLong(), any(), any());
    }

    @Test
    void getAllUserBookingShouldReturnFuture() {
        when(userService.getUserById(2L)).thenReturn(new UserDto());
        when(bookingRepository.findByBookerIdAndStartIsAfter(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.mapBookingToResponseDto(booking)).thenReturn(new BookingResponseDto());

        List<BookingResponseDto> result = bookingService.getAllUserBooking(2L, BookingState.FUTURE);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByBookerIdAndStartIsAfter(anyLong(), any(), any());
    }

    @Test
    void getAllUserBookingShouldReturnCurrent() {
        when(userService.getUserById(2L)).thenReturn(new UserDto());
        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.mapBookingToResponseDto(booking)).thenReturn(new BookingResponseDto());

        List<BookingResponseDto> result = bookingService.getAllUserBooking(2L, BookingState.CURRENT);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByBookerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any());
    }

    @Test
    void getAllUserItemBookingShouldReturnAll() {
        when(userService.getUserById(1L)).thenReturn(new UserDto());
        when(bookingRepository.findAllByOwnerId(1L)).thenReturn(List.of(booking));
        when(bookingMapper.mapBookingToResponseDto(booking)).thenReturn(new BookingResponseDto());

        List<BookingResponseDto> result = bookingService.getAllUserItemBooking(1L, BookingState.ALL);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findAllByOwnerId(1L);
    }

    @Test
    void getAllUserItemBookingShouldReturnCurrent() {
        when(userService.getUserById(1L)).thenReturn(new UserDto());
        when(bookingRepository.findCurrentByOwner(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.mapBookingToResponseDto(booking)).thenReturn(new BookingResponseDto());

        List<BookingResponseDto> result = bookingService.getAllUserItemBooking(1L, BookingState.CURRENT);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findCurrentByOwner(anyLong(), any(), any());
    }

    @Test
    void getAllUserItemBookingShouldReturnPast() {
        when(userService.getUserById(1L)).thenReturn(new UserDto());
        when(bookingRepository.findPastByOwner(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.mapBookingToResponseDto(booking)).thenReturn(new BookingResponseDto());

        List<BookingResponseDto> result = bookingService.getAllUserItemBooking(1L, BookingState.PAST);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findPastByOwner(anyLong(), any(), any(), any());
    }

    @Test
    void getAllUserItemBookingShouldReturnFuture() {
        when(userService.getUserById(1L)).thenReturn(new UserDto());
        when(bookingRepository.findFutureByOwner(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.mapBookingToResponseDto(booking)).thenReturn(new BookingResponseDto());

        List<BookingResponseDto> result = bookingService.getAllUserItemBooking(1L, BookingState.FUTURE);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findFutureByOwner(anyLong(), any(), any());
    }

    @Test
    void getAllUserItemBookingShouldReturnWaiting() {
        when(userService.getUserById(1L)).thenReturn(new UserDto());
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));
        when(bookingRepository.findByItemInAndStatus(any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.mapBookingToResponseDto(booking)).thenReturn(new BookingResponseDto());

        List<BookingResponseDto> result = bookingService.getAllUserItemBooking(1L, BookingState.WAITING);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByItemInAndStatus(any(), eq(BookingStatus.WAITING), any());
    }

    @Test
    void getAllUserItemBookingShouldReturnRejected() {
        when(userService.getUserById(1L)).thenReturn(new UserDto());
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));
        when(bookingRepository.findByItemInAndStatus(any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.mapBookingToResponseDto(booking)).thenReturn(new BookingResponseDto());

        List<BookingResponseDto> result = bookingService.getAllUserItemBooking(1L, BookingState.REJECTED);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByItemInAndStatus(any(), eq(BookingStatus.REJECTED), any());
    }
}