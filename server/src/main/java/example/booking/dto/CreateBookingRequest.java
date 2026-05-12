package example.booking.dto;

import java.time.LocalDateTime;

public record CreateBookingRequest(
        Long itemId,
        LocalDateTime start,
        LocalDateTime end
) {
}
