package example.booking.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record CreateBookingRequest(
        @NotNull(message = "ItemId is required")
        @Positive
        Long itemId,

        @FutureOrPresent(message = "start: must be a date in the present or in the future")
        @NotNull(message = "start date is required")
        LocalDateTime start,

        @Future(message = "end: must be a date in the future")
        @NotNull(message = "end date is required")
        LocalDateTime end
) {
    @AssertTrue(message = "End date must be after start date")
    public boolean isEndAfterStart() {
        return end.isAfter(start);
    }

}
