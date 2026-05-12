package example.booking;

import example.BaseClient;
import example.booking.dto.BookingState;
import example.booking.dto.CreateBookingRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

@Component
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String url, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(url + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(long bookerId, CreateBookingRequest body) {
        return post("", bookerId, body);
    }

    public ResponseEntity<Object> updateStatus(long id, long bookerId, boolean approved) {
        Map<String, Object> params = Map.of("id", id, "approved", approved);
        return patch("/{id}?approved={approved}", bookerId, params, null);
    }

    public ResponseEntity<Object> getById(long id, long bookerId) {
        return get("/" + id, bookerId);
    }

    public ResponseEntity<Object> getAllForCurrentBooker(long bookerId, BookingState state) {
        Map<String, Object> params = Map.of("state", state.name());
        return get("?state={state}", bookerId, params);
    }

    public ResponseEntity<Object> getAllForOwner(long bookerId, BookingState state) {
        Map<String, Object> params = Map.of("state", state.name());
        return get("/owner?state={state}", bookerId, params);
    }
}
