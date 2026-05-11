package example.request;

import example.request.dto.CreateRequestDto;
import example.request.dto.RequestResponse;
import example.request.dto.RequestResponseWithItems;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/requests")
public class RequestController {
    private final RequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestResponse create(@RequestHeader("X-Sharer-User-Id") long requestorId,
                                  @RequestBody CreateRequestDto requestDto) {
        log.info("POST /requests started: requestorId={}", requestorId);
        RequestResponse response = service.create(requestorId, requestDto);
        log.info("POST /requests finished: requestorId={}, requestId={}", requestorId, response.id());
        return response;
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestResponseWithItems> getAll() {
        log.info("GET /requests started: ");
        List<RequestResponseWithItems> responses = service.getAll();
        log.info("GET /requests finished: size={}", responses.size());
        return responses;
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RequestResponseWithItems getById(@PathVariable long id) {
        log.info("GET /requests/{} started:", id);
        RequestResponseWithItems requests = service.getById(id);
        log.info("GET /requests/{} finished:", id);
        return requests;
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<RequestResponseWithItems> getByRequestor(@RequestHeader("X-Sharer-User-Id") long requestorId) {
        log.info("GET /requests started: requestorId={}", requestorId);
        List<RequestResponseWithItems> responses = service.getByRequestorId(requestorId);
        log.info("GET /requests finished: size={}", responses);
        return responses;
    }

}
