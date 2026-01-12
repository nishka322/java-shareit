package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.WrongRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentAndBookingDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.comment.NewCommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock private ItemRepository itemRepository;
    @Mock private UserService userService;
    @Mock private ItemMapper itemMapper;
    @Mock private UserMapper userMapper;
    @Mock private CommentMapper commentMapper;
    @Mock private CommentRepository commentRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private BookingMapper bookingMapper;
    @Mock private ItemRequestRepository itemRequestRepository;

    @InjectMocks private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("User")
                .email("user@email.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .ownerId(1L)
                .build();

        itemDto = ItemDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        itemRequest = new ItemRequest();
        itemRequest.setId(10L);
    }

    @Test
    void getItemEntityByIdShouldReturnItemWhenExists() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Item result = itemService.getItemEntityById(1L);

        assertThat(result).isEqualTo(item);
        verify(itemRepository).findById(1L);
    }

    @Test
    void getItemEntityByIdShouldThrowNotFoundExceptionWhenNotExists() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItemEntityById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь не найдена");
    }

    @Test
    void getItemDtoByIdShouldReturnItemDtoWhenExists() {
        ItemDto expectedDto = ItemDto.builder().id(1L).name("Item").build();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.mapToDto(item)).thenReturn(expectedDto);

        ItemDto result = itemService.getItemDtoById(1L);

        assertThat(result).isEqualTo(expectedDto);
        verify(itemRepository).findById(1L);
        verify(itemMapper).mapToDto(item);
    }

    @Test
    void getItemDtoByIdShouldThrowNotFoundExceptionWhenNotExists() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItemDtoById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Предмет с таким id(999) не найден");
    }

    @Test
    void createItemShouldReturnItemDtoWhenValid() {
        ItemDto savedDto = ItemDto.builder().id(1L).name("Item").build();
        when(userService.getUserById(1L)).thenReturn(new UserDto());
        when(itemMapper.mapToItem(itemDto)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.mapToDto(item)).thenReturn(savedDto);

        ItemDto result = itemService.createItem(1L, itemDto);

        assertThat(result).isEqualTo(savedDto);
        verify(userService).getUserById(1L);
        verify(itemRepository).save(item);
        assertThat(item.getOwnerId()).isEqualTo(1L);
    }

    @Test
    void createItemWithRequestIdShouldSetRequestWhenRequestExists() {
        itemDto.setRequestId(10L);
        ItemDto savedDto = ItemDto.builder().id(1L).build();
        when(userService.getUserById(1L)).thenReturn(new UserDto());
        when(itemMapper.mapToItem(itemDto)).thenReturn(item);
        when(itemRequestRepository.findById(10L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.mapToDto(item)).thenReturn(savedDto);

        itemService.createItem(1L, itemDto);

        verify(itemRequestRepository).findById(10L);
        assertThat(item.getRequest()).isEqualTo(itemRequest);
    }

    @Test
    void createItemWithRequestIdShouldThrowNotFoundExceptionWhenRequestNotExists() {
        itemDto.setRequestId(999L);
        when(userService.getUserById(1L)).thenReturn(new UserDto());
        when(itemMapper.mapToItem(itemDto)).thenReturn(item);
        when(itemRequestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.createItem(1L, itemDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Запрос с id 999 не найден");

        verify(itemRequestRepository).findById(999L);
    }

    @Test
    void editItemShouldUpdateWhenUserIsOwner() {
        ItemDto updateDto = ItemDto.builder()
                .name("Updated")
                .description("Updated description")
                .available(false)
                .build();
        ItemDto updatedDto = ItemDto.builder().id(1L).name("Updated").build();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.updateItem(item, updateDto)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.mapToDto(item)).thenReturn(updatedDto);

        ItemDto result = itemService.editItem(1L, updateDto, 1L);

        assertThat(result).isEqualTo(updatedDto);
        verify(itemRepository).save(item);
    }

    @Test
    void editItemShouldThrowNotFoundExceptionWhenUserIsNotOwner() {
        item.setOwnerId(2L);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemDto updateDto = ItemDto.builder().build();

        assertThatThrownBy(() -> itemService.editItem(1L, updateDto, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не является собственником");
    }

    @Test
    void editItemShouldThrowNotFoundExceptionWhenItemNotExists() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        ItemDto updateDto = ItemDto.builder().build();

        assertThatThrownBy(() -> itemService.editItem(1L, updateDto, 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Предмет с таким id(999) не найден");
    }

    @Test
    void searchShouldReturnEmptyListWhenTextIsBlank() {
        List<ItemDto> result = itemService.search("");

        assertThat(result).isEmpty();
        verify(itemRepository, never()).searchByText(any());
    }

    @Test
    void searchShouldReturnEmptyListWhenTextIsNull() {
        List<ItemDto> result = itemService.search(null);

        assertThat(result).isEmpty();
        verify(itemRepository, never()).searchByText(any());
    }

    @Test
    void searchShouldReturnItemsWhenTextIsValid() {
        List<Item> items = List.of(item);
        ItemDto itemDto = ItemDto.builder().id(1L).name("Item").build();
        when(itemRepository.searchByText("drill")).thenReturn(items);
        when(itemMapper.mapToDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemService.search("drill");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(itemDto);
        verify(itemRepository).searchByText("drill");
    }

    @Test
    void getAllUserItemsShouldReturnEmptyListWhenUserHasNoItems() {
        when(userService.getUserById(1L)).thenReturn(new UserDto());
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of());

        List<ItemWithCommentAndBookingDto> result = itemService.getAllUserItems(1L);

        assertThat(result).isEmpty();
        verify(userService).getUserById(1L);
        verify(itemRepository).findByOwnerId(1L);
    }

    @Test
    void getAllUserItemsShouldReturnItemsWithCommentsAndBookings() {
        ItemWithCommentAndBookingDto expectedDto = ItemWithCommentAndBookingDto.builder().id(1L).build();
        when(userService.getUserById(1L)).thenReturn(new UserDto());
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));
        when(commentRepository.findByItemIn(any(), any())).thenReturn(List.of());
        when(bookingRepository.findByItemInAndStatus(any(), any(), any())).thenReturn(List.of());
        when(itemMapper.mapItemToItemWithBooking(any(), any(), any(), any())).thenReturn(expectedDto);

        List<ItemWithCommentAndBookingDto> result = itemService.getAllUserItems(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(expectedDto);
        verify(userService).getUserById(1L);
        verify(itemRepository).findByOwnerId(1L);
    }

    @Test
    void addCommentShouldThrowWrongRequestExceptionWhenUserDidNotBookItem() {
        when(bookingRepository.hasUserBookedItem(1L, 1L)).thenReturn(false);

        NewCommentDto commentDto = NewCommentDto.builder().text("Comment").build();

        assertThatThrownBy(() -> itemService.addComment(1L, 1L, commentDto))
                .isInstanceOf(WrongRequestException.class)
                .hasMessageContaining("не может оставить на него отзыв");

        verify(bookingRepository).hasUserBookedItem(1L, 1L);
    }

    @Test
    void getItemWithCommentByIdShouldReturnDtoWithoutBookingsWhenUserIsNotOwner() {
        item.setOwnerId(2L);
        ItemWithCommentAndBookingDto expectedDto = ItemWithCommentAndBookingDto.builder()
                .id(1L)
                .lastBooking(null)
                .nextBooking(null)
                .comments(List.of())
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(1L)).thenReturn(List.of());
        when(bookingRepository.findByItemId(1L)).thenReturn(List.of());
        when(itemMapper.mapItemToItemWithBooking(any(), any(), any(), any())).thenReturn(expectedDto);

        ItemWithCommentAndBookingDto result = itemService.getItemWithCommentById(1L, 1L);

        assertThat(result).isEqualTo(expectedDto);
        verify(itemRepository).findById(1L);
        verify(commentRepository).findByItemId(1L);
    }

    @Test
    void getLastNextBookingShouldReturnNullsWhenNoBookings() {
        Item item = Item.builder().id(1L).ownerId(1L).build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemId(1L)).thenReturn(List.of());
        when(commentRepository.findByItemId(1L)).thenReturn(List.of());
        when(itemMapper.mapItemToItemWithBooking(any(), any(), any(), any()))
                .thenReturn(ItemWithCommentAndBookingDto.builder()
                        .id(1L)
                        .name("Item")
                        .description("Description")
                        .available(true)
                        .lastBooking(null)
                        .nextBooking(null)
                        .comments(List.of())
                        .build());

        ItemWithCommentAndBookingDto result = itemService.getItemWithCommentById(1L, 1L);

        assertThat(result).isNotNull();
    }

    @Test
    void getLastNextBookingShouldReturnLastBookingWhenPastBookingExists() {
        Booking pastBooking = new Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        pastBooking.setStatus(BookingStatus.APPROVED);

        BookingDateDto mockDto = mock(BookingDateDto.class);
        Item item = Item.builder().id(1L).ownerId(1L).build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemId(1L)).thenReturn(List.of(pastBooking));
        when(commentRepository.findByItemId(1L)).thenReturn(List.of());
        when(bookingMapper.mapBookingToDateDto(any())).thenReturn(mockDto);
        when(itemMapper.mapItemToItemWithBooking(any(), any(), any(), any()))
                .thenReturn(ItemWithCommentAndBookingDto.builder()
                        .id(1L)
                        .name("Item")
                        .description("Description")
                        .available(true)
                        .lastBooking(mockDto)
                        .nextBooking(null)
                        .comments(List.of())
                        .build());

        ItemWithCommentAndBookingDto result = itemService.getItemWithCommentById(1L, 1L);

        assertThat(result).isNotNull();
    }

    @Test
    void addCommentShouldSuccessWhenUserBookedItem() {
        NewCommentDto commentDto = NewCommentDto.builder().text("Great item!").build();
        UserDto userDto = UserDto.builder().id(1L).name("User").build();
        Comment comment = new Comment();
        CommentResponseDto commentResponse = CommentResponseDto.builder().id(1L).text("Great item!").build();

        when(bookingRepository.hasUserBookedItem(1L, 1L)).thenReturn(true);
        when(userService.getUserById(1L)).thenReturn(userDto);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentMapper.mapNewCommentToComment(any(), any(), any(), any())).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.mapCommentToResponse(comment)).thenReturn(commentResponse);

        CommentResponseDto result = itemService.addComment(1L, 1L, commentDto);

        assertThat(result).isEqualTo(commentResponse);
        verify(bookingRepository).hasUserBookedItem(1L, 1L);
        verify(commentRepository).save(comment);
    }

    @Test
    void getAllUserItemsShouldHandleNullCommentsAndBookings() {
        Item item2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Desc2")
                .available(true)
                .ownerId(1L)
                .build();

        when(userService.getUserById(1L)).thenReturn(new UserDto());
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item, item2));
        when(commentRepository.findByItemIn(any(), any())).thenReturn(List.of());
        when(bookingRepository.findByItemInAndStatus(any(), any(), any())).thenReturn(List.of());

        when(itemMapper.mapItemToItemWithBooking(any(), any(), any(), any()))
                .thenAnswer(invocation -> {
                    Item itemArg = invocation.getArgument(0);
                    return ItemWithCommentAndBookingDto.builder()
                            .id(itemArg.getId())
                            .name(itemArg.getName())
                            .description(itemArg.getDescription())
                            .available(itemArg.isAvailable())
                            .lastBooking(null)
                            .nextBooking(null)
                            .comments(List.of())
                            .build();
                });

        List<ItemWithCommentAndBookingDto> result = itemService.getAllUserItems(1L);

        assertThat(result).hasSize(2);
    }
}