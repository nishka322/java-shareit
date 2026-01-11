package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(long userId);

    @Query("SELECT b FROM Booking b JOIN b.item i WHERE i.ownerId = :userId")
    List<Booking> findAllByOwnerId(@Param("userId") long userId);

    List<Booking> findByItemId(long itemId);

    List<Booking> findByBookerIdAndEndIsBefore(long userId, LocalDateTime time, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(long userId, LocalDateTime time, Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime now1, LocalDateTime now2, Sort sort);

    List<Booking> findByBookerIdAndStatus(long userId, BookingStatus status, Sort sort);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId " +
            "AND b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED " +
            "AND b.end < CURRENT_TIMESTAMP")
    boolean hasUserBookedItem(@Param("userId") Long userId,
                              @Param("itemId") Long itemId);

    @Query("SELECT b FROM Booking b WHERE b.item IN :items AND b.status = :status")
    List<Booking> findByItemInAndStatus(@Param("items") Collection<Item> items,
                                        @Param("status") BookingStatus status,
                                        Sort sort);

    @Query("SELECT b FROM Booking b JOIN b.item i WHERE i.ownerId = :userId " +
            "AND b.start <= :now AND b.end > :now")
    List<Booking> findCurrentByOwner(@Param("userId") long userId,
                                     @Param("now") LocalDateTime now,
                                     Sort sort);

    @Query("SELECT b FROM Booking b JOIN b.item i WHERE i.ownerId = :userId " +
            "AND b.end < :now AND b.status = :status")
    List<Booking> findPastByOwner(@Param("userId") long userId,
                                  @Param("now") LocalDateTime now,
                                  @Param("status") BookingStatus status,
                                  Sort sort);

    @Query("SELECT b FROM Booking b JOIN b.item i WHERE i.ownerId = :userId " +
            "AND b.start > :now")
    List<Booking> findFutureByOwner(@Param("userId") long userId,
                                    @Param("now") LocalDateTime now,
                                    Sort sort);
}