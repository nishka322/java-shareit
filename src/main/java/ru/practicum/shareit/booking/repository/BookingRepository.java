package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking save(Booking booking);

    Booking findById(long id);

    List<Booking> findByBookerId(long userId);

    @Query("SELECT b FROM Booking b JOIN b.item i WHERE i.ownerId = :userId")
    List<Booking> findAllByOwnerId(@Param("userId") long userId);

    List<Booking> findByItemId(long itemId);
}