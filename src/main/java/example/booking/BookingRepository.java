package example.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    List<Booking> findAllByBookerId(Long bookerId);

    default List<Booking> findCurrentByBookerId(Long bookerId, OffsetDateTime currentTime) {
        QBooking booking = QBooking.booking;
        BooleanExpression expression = booking.booker.id.eq(bookerId)
                .and(booking.start.loe(currentTime))
                .and(booking.end.goe(currentTime));

        return toList(findAll(expression));
    }

    default List<Booking> findPastByBookerId(Long bookerId, OffsetDateTime currentTime) {
        QBooking booking = QBooking.booking;
        BooleanExpression expression = booking.booker.id.eq(bookerId)
                .and(booking.end.lt(currentTime));

        return toList(findAll(expression));
    }

    default List<Booking> findFutureByBookerId(Long bookerId, OffsetDateTime currentTime) {
        QBooking booking = QBooking.booking;
        BooleanExpression expression = booking.booker.id.eq(bookerId)
                .and(booking.start.gt(currentTime));

        return toList(findAll(expression));
    }

    default List<Booking> findByBookingStatus(Long bookerId, BookingStatus status) {
        QBooking booking = QBooking.booking;
        BooleanExpression expression = booking.booker.id.eq(bookerId)
                .and(booking.status.eq(status));

        return toList(findAll(expression));
    }

    default <T> List<T> toList(Iterable<T> iterable) {
        List<T> result = new ArrayList<>();
        iterable.forEach(result::add);
        return result;
    }
}
