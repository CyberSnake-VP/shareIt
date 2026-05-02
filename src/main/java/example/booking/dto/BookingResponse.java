package example.booking.dto;

import example.booking.BookingStatus;

import java.time.OffsetDateTime;

public record BookingResponse(
        Long id,
        BookingResponseShortItem item,
        BookingResponseShortUser booker,
        OffsetDateTime start,
        OffsetDateTime end,
        BookingStatus status

) {
}
