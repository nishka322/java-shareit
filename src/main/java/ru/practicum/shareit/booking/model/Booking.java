package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User booker;

    @Column(name = "start_time", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime start;
    @Column(name = "end_time", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime end;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private BookingStatus status;
}