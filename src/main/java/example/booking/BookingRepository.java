package example.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    List<Booking> findAllByBookerId(Long bookerId);

    default Optional<Booking> findBookingByBookerAndItem(Long bookerId, Long itemId, OffsetDateTime currentTime) {
        QBooking booking = QBooking.booking;
        BooleanExpression expression = booking.booker.id.eq(bookerId)
                .and(booking.item.id.eq(itemId))
                .and(booking.status.eq(BookingStatus.APPROVED))
                .and(booking.start.loe(currentTime));
        return findOne(expression);
    }

    // ========== ДЛЯ БУКЕРА (кто бронирует) ==========

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


    // ========== ДЛЯ ВЛАДЕЛЬЦА ВЕЩИ ==========

    List<Booking> findAllByItemOwnerId(Long ownerId);

    default List<Booking> findCurrentByOwnerId(Long ownerId, OffsetDateTime currentTime) {
        QBooking booking = QBooking.booking;
        BooleanExpression expression = booking.item.owner.id.eq(ownerId)
                .and(booking.start.loe(currentTime))
                .and(booking.end.goe(currentTime));

        return toList(findAll(expression));
    }

    default List<Booking> findPastByOwnerId(Long ownerId, OffsetDateTime currentTime) {
        QBooking booking = QBooking.booking;
        BooleanExpression expression = booking.item.owner.id.eq(ownerId)
                .and(booking.end.lt(currentTime));

        return toList(findAll(expression));
    }

    default List<Booking> findFutureByOwnerId(Long ownerId, OffsetDateTime currentTime) {
        QBooking booking = QBooking.booking;
        BooleanExpression expression = booking.item.owner.id.eq(ownerId)
                .and(booking.start.gt(currentTime));

        return toList(findAll(expression));
    }

    default List<Booking> findByBookingStatusByOwnerId(Long ownerId, BookingStatus status) {
        QBooking booking = QBooking.booking;
        BooleanExpression expression = booking.item.owner.id.eq(ownerId)
                .and(booking.status.eq(status));

        return toList(findAll(expression));
    }


    // ========== ВСПОМОГАТЕЛЬНЫЙ МЕТОД ==========

    default <T> List<T> toList(Iterable<T> iterable) {
        List<T> result = new ArrayList<>();
        iterable.forEach(result::add);
        return result;
    }
}
