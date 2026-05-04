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
        ZoneId clientZone = ZoneId.of("Europe/Moscow");
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
                booking.getStart().atZoneSameInstant(clientZone).toLocalDateTime(),
                booking.getEnd().atZoneSameInstant(clientZone).toLocalDateTime(),
                booking.getStatus()
        );
    }

    // я считаю что с сервера (клиента) приходит время в utc, потому что без временной зоны в тестах
    // поэтому привожу работу приложения к единому времени, получил значит типа UTC значит буду считать это время как UTC
    public static Booking toBooking(CreateBookingRequest request, Item item, User booker) {
        ZoneId clientZone = ZoneId.of("Europe/Moscow");
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(
                request.start()
                        .atZone(clientZone)
                        .toOffsetDateTime()
                        .withOffsetSameInstant(ZoneOffset.UTC));
        booking.setEnd(
                request.end()
                        .atZone(clientZone)
                        .toOffsetDateTime()
                        .withOffsetSameInstant(ZoneOffset.UTC));
        return booking;
    }
}
