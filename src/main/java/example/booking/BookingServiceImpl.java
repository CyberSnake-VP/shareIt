package example.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import example.booking.dto.BookingMapper;
import example.booking.dto.BookingResponse;
import example.booking.dto.CreateBookingRequest;
import example.exception.BookingConflictException;
import example.exception.NotAvailableException;
import example.exception.NotFoundException;
import example.item.Item;
import example.item.ItemRepository;
import example.user.User;
import example.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private static final String USER_NOT_FOUND = "User not found";
    private static final String ITEM_NOT_FOUND = "Item not found";
    private static final String ITEM_NOT_AVAILABLE = "Item is not available for booking";
    private static final String BOOKING_DATE_CONFLICT = "Item is already booked for these dates";
    private static final String BOOKING_NOT_FOUND = "Booking not found";
    private static final String NOT_OWNER_ITEM = "Only item owner can approve/reject bookings";
    private static final String BOOKING_STATUS_CONFLICT = "Booking must be have status WAITING";
    private static final String NOT_OWNER_BOOKER = "User not be owner item or booker";
    private static final String STATE_INCORRECT = "Booking state is incorrect";


    @Override
    public BookingResponse create(Long userId, CreateBookingRequest request) {
        log.info("Create booking started: userId={}, itemId={}", userId, request.itemId());

        log.debug("Create booking: checking user");
        User existingUser = getUserByIdOrThrow(userId);

        log.debug("Create booking: checking item");
        Item existingItem = getItemByIdOrThrow(request.itemId());

        if (!existingItem.isAvailable()) {
            log.warn("Create booking failed: item not available, itemId={}", request.itemId());
            throw new NotAvailableException(ITEM_NOT_AVAILABLE);
        }

        Booking booking = BookingMapper.toBooking(request, existingItem, existingUser);

        // Собираем условие, проверяем на пересечение по времени и на статус занятости, используем queryDsl
        BooleanExpression expression = QBooking.booking.item.id.eq(request.itemId())
                .and(QBooking.booking.status.in(BookingStatus.APPROVED, BookingStatus.WAITING))
                .and(QBooking.booking.start.before(booking.getEnd()))
                .and(QBooking.booking.end.after(booking.getStart()));

        boolean isDataConflict = bookingRepository.exists(expression);

        if (isDataConflict) {
            log.warn("Create booking failed: date conflict, itemId={}", request.itemId());
            throw new BookingConflictException(BOOKING_DATE_CONFLICT);
        }

        // сохраняем бронирование
        Booking saved = bookingRepository.save(booking);

        log.info("Create booking completed: userId={}, itemId={}", userId, request.itemId());
        return BookingMapper.toBookingResponse(saved);
    }


    @Override
    public BookingResponse getById(Long bookingId, Long userId) {
        log.info("Get booking started: bookingId={}, userId={}", bookingId, userId);

        // проверка пользователя
        getUserByIdOrThrow(userId);

        // получаем бронирование
        Booking booking = getBookingByIdOrThrow(bookingId);

        // проверка на автора бронирования или
        boolean isBooker = booking.getBooker().getId().equals(userId);
        // проверка на владельца вещи
        boolean isOwnerItem = booking.getItem().getOwner().getId().equals(userId);

        // если не автор бронирования и не владелец вещи
        if (!isBooker && !isOwnerItem) {
            log.warn("Get booking failed: user not be owner item or booker, userId={}", userId);
            throw new BookingConflictException(NOT_OWNER_BOOKER);
        }

        return BookingMapper.toBookingResponse(booking);
    }

    @Override
    public BookingResponse updateBookingStatus(Long bookingId, Long ownerId, boolean approved) {
        log.info("Update booking status started: bookingId={}, ownerId={}, approved={}", bookingId, ownerId, approved);

        // находим бронирование
        Booking booking = getBookingByIdOrThrow(bookingId);

        // проверяем владельца вещи
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            log.warn("Update booking status failed: ");
            throw new BookingConflictException(NOT_OWNER_ITEM);
        }

        // проверяем статус бронирования и статус approved
        if (booking.getStatus() != BookingStatus.WAITING) {
            log.warn("Update booking status failed: booking must be have status WAITING");
            throw new BookingConflictException(BOOKING_STATUS_CONFLICT);
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        log.info("Update booking status completed: bookingId={}, ownerId={}, approved={}", bookingId, ownerId, approved);
        return BookingMapper.toBookingResponse(bookingRepository.save(booking));
    }

    @Override
    public List<BookingResponse> getAllCurrentUser(Long bookerId, BookingState state) {
        log.info("Get booking list by booker started: bookerId={}, state={}", bookerId, state);

        // получаем текущее время
        OffsetDateTime currentTime = OffsetDateTime.now();
        // проверяем пользователя
        getUserByIdOrThrow(bookerId);
        // подготавливаем список
        List<Booking> bookings;

        switch (state) {
            case ALL -> bookings = bookingRepository.findAllByBookerId(bookerId);
            case CURRENT -> bookings = bookingRepository.findCurrentByBookerId(bookerId, currentTime); // текущие
            case PAST -> bookings = bookingRepository.findPastByBookerId(bookerId, currentTime); // завершенные
            case FUTURE -> bookings = bookingRepository.findFutureByBookerId(bookerId, currentTime); // будущие
            case WAITING -> bookings = bookingRepository.findByBookingStatus(bookerId, BookingStatus.WAITING); // ожидающие подтверждения
            case REJECTED -> bookings = bookingRepository.findByBookingStatus(bookerId, BookingStatus.REJECTED); // отклоненные
            default -> {
                log.warn("Get booking list by booker failed: state is incorrect, state={}", state);
                throw new BookingConflictException(STATE_INCORRECT);
            }
        }

        log.info("Get booking list by booker completed: bookerId={}, state={}", bookerId, state);
        return bookings.stream().map(BookingMapper::toBookingResponse).toList();
    }

    @Override
    public List<BookingResponse> getAllByOwnerItem(Long ownerId, BookingState state) {
        log.info("Get booking list by owner started: ownerId={}, state={}", ownerId, state);

        // получаем текущее время
        OffsetDateTime currentTime = OffsetDateTime.now();
        // проверяем пользователя
        getUserByIdOrThrow(ownerId);
        // формируем список
        List<Booking> bookings;

        switch (state) {
            case ALL -> bookings = bookingRepository.findAllByItemOwnerId(ownerId);
            case CURRENT -> bookings = bookingRepository.findCurrentByOwnerId(ownerId, currentTime); // текущие
            case PAST -> bookings = bookingRepository.findPastByOwnerId(ownerId, currentTime); // завершенные
            case FUTURE -> bookings = bookingRepository.findFutureByOwnerId(ownerId, currentTime); // будущие
            case WAITING -> bookings = bookingRepository.findByBookingStatusByOwnerId(ownerId, BookingStatus.WAITING); // ожидающие подтверждения
            case REJECTED -> bookings = bookingRepository.findByBookingStatusByOwnerId(ownerId, BookingStatus.REJECTED); // отклоненные
            default -> {
                log.warn("Get booking list by owner failed: state is incorrect, state={}", state);
                throw new BookingConflictException(STATE_INCORRECT);
            }
        }

        log.info("Get booking list by owner completed: ownerId={}, state={}", ownerId, state);
        return bookings.stream().map(BookingMapper::toBookingResponse).toList();
    }


    private User getUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Get user failed: user not found, userId={}", userId);
                    return new NotFoundException(USER_NOT_FOUND);
                });
    }

    private Item getItemByIdOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Get item failed: item not found, itemId={}", itemId);
                    return new NotFoundException(ITEM_NOT_FOUND);
                });
    }

    private Booking getBookingByIdOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Update booking status failed: booking not found, bookingId={}", bookingId);
                    return new NotFoundException(BOOKING_NOT_FOUND);
                });
    }
}
