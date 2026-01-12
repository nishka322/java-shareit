
package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Qualifier;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    Booking mapBookingDtoToBooking(BookingDto dto);

    @Mapping(target = "item.id", source = "itemId")
    Booking mapRequestDtoToBooking(BookingRequestDto dto);

    @Mapping(target = "item", source = "booking.item")
    @Mapping(target = "booker", source = "booking.booker")
    @Mapping(target = "status", source = "status")
    BookingDto mapBookingToBookingDto(Booking booking);

    @Mapping(target = "item", source = "booking.item")
    @Mapping(target = "booker", source = "booking.booker")
    @Mapping(target = "status", source = "status")
    BookingResponseDto mapBookingToResponseDto(Booking booking);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "start", source = "start")
    BookingDateDto mapBookingToDateDto(Booking booking);

    @Qualifier
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    public @interface ItemMapper {}
}