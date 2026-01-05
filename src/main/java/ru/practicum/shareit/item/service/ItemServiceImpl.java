package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.WrongRequestException;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.comment.NewCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentAndBookingDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.DbItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final DbItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    public ItemServiceImpl(@Qualifier("DbItemRepo") DbItemRepository itemRepository,
                           UserService userService,
                           ItemMapper itemMapper,
                           UserMapper userMapper,
                           CommentMapper commentMapper,
                           CommentRepository commentRepository,
                           BookingRepository bookingRepository,
                           BookingMapper bookingMapper) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    @Transactional
    public ItemDto createItem(long userId, ItemDto itemDto) {
        log.debug("Creating item for user {}, DTO: {}", userId, itemDto);
        try {
            userService.getUserById(userId);
            log.debug("User exists");

            Item item = itemMapper.mapToItem(itemDto);
            log.debug("Mapped to item: {}", item);

            item.setOwnerId(userId);
            log.debug("Set ownerId: {}", userId);

            Item savedItem = itemRepository.save(item);
            log.debug("Saved item with id: {}", savedItem.getId());

            ItemDto result = itemMapper.mapToDto(savedItem);
            log.debug("Returning DTO: {}", result);

            return result;
        } catch (Exception e) {
            log.error("Error creating item: ", e);
            throw e;
        }
    }

    @Override
    public ItemDto getItemDtoById(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с таким id(" + itemId + ") не найден"));
        return itemMapper.mapToDto(item);
    }

    @Override
    @Transactional
    public ItemDto editItem(long userId, ItemDto itemDto, long itemId) {
        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с таким id(" + itemId + ") не найден"));

        if (oldItem.getOwnerId() != userId) {
            log.info("Попытка редактировать карточку предмета другого пользователя");
            throw new NotFoundException("Пользователь не является собственником данной вещи");
        }

        Item newItem = itemMapper.updateItem(oldItem, itemDto);
        return itemMapper.mapToDto(itemRepository.save(newItem));
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.searchByText(text);
        return items.stream()
                .map(itemMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(Long userId, long itemId, NewCommentDto dto) {
        checkUserHasBooking(userId, itemId);

        Item item = getItemEntityById(itemId);
        Comment comment = commentMapper.mapNewCommentToComment(
                dto,
                userMapper.toEntity(userService.getUserById(userId)),
                item,
                LocalDateTime.now()
        );

        return commentMapper.mapCommentToResponse(commentRepository.save(comment));
    }

    @Override
    public List<ItemWithCommentAndBookingDto> getAllUserItems(long userId) {
        userService.getUserById(userId);

        List<Item> items = itemRepository.findByOwnerId(userId);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Comment> comments = commentRepository.findByItemIn(items, Sort.by(Sort.Direction.DESC, "created"));

        List<Booking> bookings = bookingRepository.findByItemInAndStatus(
                items,
                BookingStatus.APPROVED,
                Sort.by(Sort.Direction.DESC, "start")
        );

        Map<Item, List<Comment>> commentsByItem = comments.stream()
                .collect(Collectors.groupingBy(Comment::getItem));

        Map<Item, List<Booking>> bookingsByItem = bookings.stream()
                .collect(Collectors.groupingBy(Booking::getItem));

        return items.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItem.getOrDefault(item, Collections.emptyList());

                    BookingDateDto lastBooking = null;
                    BookingDateDto nextBooking = null;

                    LocalDateTime now = LocalDateTime.now();

                    List<Booking> pastBookings = itemBookings.stream()
                            .filter(b -> !b.getStart().isAfter(now))
                            .toList();

                    if (!pastBookings.isEmpty()) {
                        Booking last = pastBookings.stream()
                                .max(Comparator.comparing(Booking::getStart))
                                .orElse(null);
                        lastBooking = bookingMapper.mapBookingToDateDto(last);
                    }

                    List<Booking> futureBookings = itemBookings.stream()
                            .filter(b -> b.getStart().isAfter(now))
                            .toList();

                    if (!futureBookings.isEmpty()) {
                        Booking next = futureBookings.stream()
                                .min(Comparator.comparing(Booking::getStart))
                                .orElse(null);
                        nextBooking = bookingMapper.mapBookingToDateDto(next);
                    }

                    List<CommentResponseDto> itemComments = commentsByItem.getOrDefault(item, Collections.emptyList())
                            .stream()
                            .map(commentMapper::mapCommentToResponse)
                            .collect(Collectors.toList());

                    return itemMapper.mapItemToItemWithBooking(
                            item,
                            lastBooking,
                            nextBooking,
                            itemComments
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemWithCommentAndBookingDto getItemWithCommentById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        List<Booking> itemBookings = bookingRepository.findByItemId(itemId);

        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentResponseDto> itemComments = comments.stream()
                .map(commentMapper::mapCommentToResponse)
                .collect(Collectors.toList());

        BookingDateDto lastBooking = null;
        BookingDateDto nextBooking = null;

        if (userId == item.getOwnerId()) {
            List<BookingDateDto> lastNextBooking = getLastNextBooking(itemBookings);
            lastBooking = lastNextBooking.get(0);
            nextBooking = lastNextBooking.get(1);
        }

        return itemMapper.mapItemToItemWithBooking(item, lastBooking, nextBooking, itemComments);
    }

    private void checkUserHasBooking(long userId, long itemId) {
        if (!bookingRepository.hasUserBookedItem(userId, itemId)) {
            throw new WrongRequestException("Пользователь, который еще не пользовался предметом, не может оставить на него отзыв.");
        }
    }

    private List<BookingDateDto> getLastNextBooking(List<Booking> bookings) {
        LocalDateTime now = LocalDateTime.now();

        if (bookings == null || bookings.isEmpty()) {
            return Arrays.asList(null, null);
        }

        BookingDateDto lastBooking = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .filter(b -> b.getEnd().isBefore(now))
                .max(Comparator.comparing(Booking::getEnd))
                .map(bookingMapper::mapBookingToDateDto)
                .orElse(null);

        BookingDateDto nextBooking = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .filter(b -> b.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .map(bookingMapper::mapBookingToDateDto)
                .orElse(null);

        return Arrays.asList(lastBooking, nextBooking);
    }

    @Override
    public Item getItemEntityById(Long itemId) {
        log.info("Получение сущности вещи с ID: {}", itemId);
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }
}