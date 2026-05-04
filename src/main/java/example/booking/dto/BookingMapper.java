package example.booking.dto;

import example.booking.Booking;
import example.item.Item;
import example.user.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZoneId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingResponse toBookingResponse(Booking booking) {
        if (booking == null) {
            return null;
        }

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
                booking.getStart().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime(),
                booking.getEnd().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(CreateBookingRequest request, Item item, User booker) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(request.start().atZone(ZoneId.systemDefault()).toOffsetDateTime());
        booking.setEnd(request.end().atZone(ZoneId.systemDefault()).toOffsetDateTime());
        return booking;
    }
}
