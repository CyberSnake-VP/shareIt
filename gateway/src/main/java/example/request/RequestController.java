package example.request;

import example.request.dto.CreateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {
    private final RequestClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(RequestClient.USER_ID_HEADER) long requestorId,
                                         @RequestBody @Valid CreateRequestDto body) {
        log.info("POST /request on server started for requestorId={}", requestorId);
        ResponseEntity<Object> response = client.create(requestorId, body);
        log.info("POST /request on server finished for requestorId={}", requestorId);
        return response;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll() {
        log.info("GET /requests/all on server started");
        ResponseEntity<Object> response = client.getAll();
        log.info("GET /requests/all on server finished");
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable long id) {
        log.info("GET /requests/{} on server started", id);
        ResponseEntity<Object> response = client.getById(id);
        log.info("GET /requests/{} on server finished", id);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getByRequestor(@RequestHeader(RequestClient.USER_ID_HEADER) long requestorId) {
        log.info("GET /requests on server started for requestorId={}", requestorId);
        ResponseEntity<Object> response = client.getByRequestor(requestorId);
        log.info("GET /requests on server finished for requestorId={}", requestorId);
        return response;
    }
}
