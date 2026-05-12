package example.booking;

import example.booking.dto.BookingState;
import example.booking.dto.CreateBookingRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(BookingClient.USER_ID_HEADER) long bookerId,
                                         @RequestBody @Valid CreateBookingRequest request) {
        log.info("POST /bookings on server started for bookerId={}", bookerId);
        ResponseEntity<Object> response = client.create(bookerId, request);
        log.info("POST /bookings on server finished for bookerId={}", bookerId);
        return response;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateStatus(@PathVariable long id,
                                               @RequestHeader(BookingClient.USER_ID_HEADER) long bookerId,
                                               @RequestParam(name = "approved") boolean approved) {
        log.info("PATCH /bookings/{} on server started for bookerId={}, approved={}", id, bookerId, approved);
        ResponseEntity<Object> response = client.updateStatus(id, bookerId, approved);
        log.info("PATCH /bookings/{} on server finished for bookerId={}, approved={}", id, bookerId, approved);
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable long id,
                                          @RequestHeader(BookingClient.USER_ID_HEADER) long bookerId) {
        log.info("GET /bookings/{} on server started for bookerId={}", id, bookerId);
        ResponseEntity<Object> response = client.getById(id, bookerId);
        log.info("GET /bookings/{} on server finished for bookerId={}", id, bookerId);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getAllForCurrentBooker(@RequestHeader(BookingClient.USER_ID_HEADER) long bookerId,
                                                         @RequestParam(
                                                                 name = "state",
                                                                 required = false,
                                                                 defaultValue = "ALL") BookingState state) {
        log.info("GET /bookings on server started for bookerId={}", bookerId);
        ResponseEntity<Object> response = client.getAllForCurrentBooker(bookerId, state);
        log.info("GET /bookings on server finished for bookerId={}", bookerId);
        return response;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllForOwner(@RequestHeader(BookingClient.USER_ID_HEADER) long ownerId,
                                                 @RequestParam(
                                                         name = "state",
                                                         required = false,
                                                         defaultValue = "ALL") BookingState state) {
        log.info("GET /bookings/owner on server started for ownerId={}", ownerId);
        ResponseEntity<Object> response = client.getAllForOwner(ownerId, state);
        log.info("GET /bookings/owner on server finished for ownerId={}", ownerId);
        return response;
    }
}
