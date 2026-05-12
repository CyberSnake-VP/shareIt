package example.item;

import example.booking.dto.BookingResponse;

import java.util.List;

public record ItemCommentResponse(
        Long id,
        String name,
        String description,
        Boolean available,
        BookingResponse lastBooking,
        BookingResponse nextBooking,
        List<CommentResponse> comments
) {
}
