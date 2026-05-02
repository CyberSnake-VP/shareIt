package example.booking;

import example.booking.dto.BookingResponse;
import example.booking.dto.CreateBookingRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private static final String HEADER_REQUEST = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@RequestHeader(HEADER_REQUEST) Long userId,
                                  @RequestBody @Valid CreateBookingRequest request) {
        log.info("POST /bookings started: userId={}", userId);
        BookingResponse response = bookingService.create(userId, request);
        log.info("POST /bookings finished: userId={}", userId);
        return response;
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse updateBookingStatus(@PathVariable Long bookingId,
                                               @RequestHeader(HEADER_REQUEST) Long ownerId,
                                               @RequestParam(name = "approved") boolean approved) {
        log.info("PATCH /bookings/{} started: ownerId={}, approved={}", bookingId, ownerId, approved);
        BookingResponse response = bookingService.updateBookingStatus(bookingId, ownerId, approved);
        log.info("PATCH /bookings/{} completed: ownerId={}, approved={}", bookingId, ownerId, approved);
        return response;
    }

    // Получение конкретного бронирования для текущего (или владельца) пользователя
    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse getById(@RequestHeader(HEADER_REQUEST) Long userId,
                                   @PathVariable Long bookingId) {
        log.info("GET /bookings/{} started: userid={}", bookingId, userId);
        BookingResponse response = bookingService.getById(bookingId, userId);
        log.info("GET /bookings/{} finished: userId={}", bookingId, userId);
        return response;
    }

    // Получение списка всех бронирований для текущего пользователя (автора бронирований)
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponse> getAllByCurrentUser(@RequestHeader(HEADER_REQUEST) Long bookerId,
                                                     @RequestParam(value = "state", required = false,
                                                             defaultValue = "ALL") BookingState state) {
        log.info("GET /bookings/?state={} started: userId={}", state, bookerId);
        List<BookingResponse> responses = bookingService.getAllCurrentUser(bookerId, state);
        log.info("GET /bookings/?state={} finished: userId={}, count={}", state, bookerId, responses.size());
        return responses;
    }

    // Получение владельцем списка своих забронированных вещей
    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponse> getAllByOwner(@RequestHeader(HEADER_REQUEST) Long ownerId,
                                               @RequestParam(value = "state", required = false,
                                                       defaultValue = "ALL") BookingState state) {
        log.info("GET /bookings/owner/?state={} started: ownerId={}", state, ownerId);
        List<BookingResponse> responses = bookingService.getAllByOwnerItem(ownerId, state);
        log.info("GET /bookings/owner/?state={} finished: ownerId={}", state, ownerId);
        return responses;
    }

}
