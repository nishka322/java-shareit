package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.service.BookingState;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.WrongRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;
    private Item unavailableItem;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwnerId(owner.getId());
        item = itemRepository.save(item);

        unavailableItem = new Item();
        unavailableItem.setName("Broken Drill");
        unavailableItem.setDescription("Not working");
        unavailableItem.setAvailable(false);
        unavailableItem.setOwnerId(owner.getId());
        unavailableItem = itemRepository.save(unavailableItem);
    }

    @Test
    void shouldMakeBooking() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto response = bookingService.makeBooking(requestDto, booker.getId());

        assertNotNull(response);
        assertEquals(item.getId(), response.getItem().getId());
        assertEquals(booker.getId(), response.getBooker().getId());
        assertEquals(BookingStatus.WAITING, response.getStatus());
        assertNotNull(response.getStart());
        assertNotNull(response.getEnd());
    }

    @Test
    void shouldThrowExceptionWhenBookingUnavailableItem() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(unavailableItem.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        WrongRequestException exception = assertThrows(WrongRequestException.class,
                () -> bookingService.makeBooking(requestDto, booker.getId()));
        assertEquals("Объект не доступен для бронирования.", exception.getMessage());
    }

    @Test
    void shouldApproveBooking() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingResponseDto booking = bookingService.makeBooking(requestDto, booker.getId());

        BookingResponseDto approvedBooking = bookingService.approveBooking(owner.getId(), booking.getId(), true);

        assertNotNull(approvedBooking);
        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());
    }

    @Test
    void shouldRejectBooking() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingResponseDto booking = bookingService.makeBooking(requestDto, booker.getId());

        BookingResponseDto rejectedBooking = bookingService.approveBooking(owner.getId(), booking.getId(), false);

        assertNotNull(rejectedBooking);
        assertEquals(BookingStatus.REJECTED, rejectedBooking.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenNonOwnerApprovesBooking() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingResponseDto booking = bookingService.makeBooking(requestDto, booker.getId());

        User anotherUser = new User();
        anotherUser.setName("Another");
        anotherUser.setEmail("another@example.com");
        anotherUser = userRepository.save(anotherUser);

        User finalAnotherUser = anotherUser;
        WrongRequestException exception = assertThrows(WrongRequestException.class,
                () -> bookingService.approveBooking(finalAnotherUser.getId(), booking.getId(), true));
        assertEquals("Пользователь не является собственником и не может подтверждать бронь", exception.getMessage());
    }

    @Test
    void shouldGetBookingById() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingResponseDto createdBooking = bookingService.makeBooking(requestDto, booker.getId());

        BookingResponseDto foundBooking = bookingService.getBookingById(booker.getId(), createdBooking.getId());

        assertNotNull(foundBooking);
        assertEquals(createdBooking.getId(), foundBooking.getId());
        assertEquals(item.getId(), foundBooking.getItem().getId());
    }

    @Test
    void shouldThrowExceptionWhenGettingBookingWithoutAccess() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingResponseDto booking = bookingService.makeBooking(requestDto, booker.getId());

        User stranger = new User();
        stranger.setName("Stranger");
        stranger.setEmail("stranger@example.com");
        stranger = userRepository.save(stranger);

        User finalStranger = stranger;
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(finalStranger.getId(), booking.getId()));
        assertEquals("У пользователя нет прав доступа к информации о бронировании.", exception.getMessage());
    }

    @Test
    void shouldGetAllUserBooking() {
        BookingRequestDto requestDto1 = new BookingRequestDto();
        requestDto1.setItemId(item.getId());
        requestDto1.setStart(LocalDateTime.now().plusDays(1));
        requestDto1.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.makeBooking(requestDto1, booker.getId());

        Item item2 = new Item();
        item2.setName("Hammer");
        item2.setDescription("Heavy hammer");
        item2.setAvailable(true);
        item2.setOwnerId(owner.getId());
        item2 = itemRepository.save(item2);

        BookingRequestDto requestDto2 = new BookingRequestDto();
        requestDto2.setItemId(item2.getId());
        requestDto2.setStart(LocalDateTime.now().plusDays(3));
        requestDto2.setEnd(LocalDateTime.now().plusDays(4));
        bookingService.makeBooking(requestDto2, booker.getId());

        List<BookingResponseDto> bookings = bookingService.getAllUserBooking(booker.getId(), BookingState.ALL);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
    }

    @Test
    void shouldGetAllUserItemBooking() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.makeBooking(requestDto, booker.getId());

        List<BookingResponseDto> bookings = bookingService.getAllUserItemBooking(owner.getId(), BookingState.ALL);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(item.getId(), bookings.get(0).getItem().getId());
    }

    @Test
    void shouldGetUserBookingsByState() {
        BookingRequestDto pastBooking = new BookingRequestDto();
        pastBooking.setItemId(item.getId());
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        bookingService.makeBooking(pastBooking, booker.getId());

        BookingRequestDto currentBooking = new BookingRequestDto();
        currentBooking.setItemId(item.getId());
        currentBooking.setStart(LocalDateTime.now().minusHours(1));
        currentBooking.setEnd(LocalDateTime.now().plusHours(1));
        bookingService.makeBooking(currentBooking, booker.getId());

        BookingRequestDto futureBooking = new BookingRequestDto();
        futureBooking.setItemId(item.getId());
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.makeBooking(futureBooking, booker.getId());

        List<BookingResponseDto> pastBookings = bookingService.getAllUserBooking(booker.getId(), BookingState.PAST);
        assertEquals(1, pastBookings.size());

        List<BookingResponseDto> currentBookings = bookingService.getAllUserBooking(booker.getId(), BookingState.CURRENT);
        assertEquals(1, currentBookings.size());

        List<BookingResponseDto> futureBookings = bookingService.getAllUserBooking(booker.getId(), BookingState.FUTURE);
        assertEquals(1, futureBookings.size());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(NotFoundException.class,
                () -> bookingService.makeBooking(requestDto, 999L));
    }

    @Test
    void shouldThrowExceptionWhenItemNotFound() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(999L);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(NotFoundException.class,
                () -> bookingService.makeBooking(requestDto, booker.getId()));
    }
}