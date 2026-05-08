package example.booking.dto;

import example.booking.BookingStatus;

import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        BookingResponseShortItem item,
        BookingResponseShortUser booker,
        LocalDateTime start,
        LocalDateTime end,
        BookingStatus status

) {
}
