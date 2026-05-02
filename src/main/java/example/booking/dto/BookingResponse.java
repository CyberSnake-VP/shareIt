package example.booking.dto;

import example.booking.BookingStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record BookingResponse(
        Long id,
        BookingResponseShortItem item,
        BookingResponseShortUser booker,
        LocalDateTime start,
        LocalDateTime end,
        BookingStatus status

) {
}
