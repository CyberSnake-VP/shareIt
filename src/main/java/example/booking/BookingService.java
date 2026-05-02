package example.booking;

import example.booking.dto.BookingResponse;
import example.booking.dto.CreateBookingRequest;

import java.util.List;

public interface BookingService {
    BookingResponse create(Long userId, CreateBookingRequest request);

    BookingResponse getById(Long bookingId, Long userId);

    BookingResponse updateBookingStatus(Long bookingId, Long ownerId, boolean approved);

    List<BookingResponse> getAllCurrentUser(Long userId, BookingState state);

    List<BookingResponse> getAllByOwnerItem(Long ownerId, BookingState state);
}
