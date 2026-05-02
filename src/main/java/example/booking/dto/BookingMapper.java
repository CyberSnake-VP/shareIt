package example.booking.dto;

import example.booking.Booking;
import example.item.Item;
import example.user.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZoneOffset;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingResponse toBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                new BookingResponseShortItem(
                        booking.getItem().getId(),
                        booking.getItem().getName()
                ),
                new BookingResponseShortUser(
                        booking.getBooker().getId(),
                        booking.getBooker().getEmail(),
                        booking.getBooker().getName()
                ),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(CreateBookingRequest request, Item item, User booker) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(request.start().atOffset(ZoneOffset.UTC));
        booking.setEnd(request.end().atOffset(ZoneOffset.UTC));
        return booking;
    }
}
