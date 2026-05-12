package example.item;

import example.client.ItemClient;
import example.item.dto.CreateCommentRequest;
import example.item.dto.CreateItemRequest;
import example.item.dto.UpdateItemRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient client;


    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(ItemClient.USER_ID_HEADER) long ownerId,
                                         @RequestBody @Valid CreateItemRequest request) {
        log.info("POST /items on server started");
        ResponseEntity<Object> response = client.createItem(ownerId, request);
        log.info("POST /items on server finished");
        return response;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader(ItemClient.USER_ID_HEADER) long ownerId,
                                         @PathVariable long id,
                                         @RequestBody @Valid UpdateItemRequest request) {
        log.info("PATCH /items/{} on server started", id);
        ResponseEntity<Object> response = client.updateItem(id, ownerId, request);
        log.info("PATCH /items/{} on server finished", id);
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable long id,
                                          @RequestHeader(ItemClient.USER_ID_HEADER) long ownerId) {
        log.info("GET /items/{} on server started", id);
        ResponseEntity<Object> response = client.getById(id, ownerId);
        log.info("GET /items/{} on server finished", id);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(ItemClient.USER_ID_HEADER) long ownerId) {
        log.info("GET /items on server started");
        ResponseEntity<Object> response = client.getAllByOwner(ownerId);
        log.info("GET /items on server finished");
        return response;
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(ItemClient.USER_ID_HEADER) long ownerId,
                                             @PathVariable long id,
                                             @RequestBody @Valid CreateCommentRequest request) {
        log.info("POST /items/{}/comment on server stared for ownerId={}", id, ownerId);
        ResponseEntity<Object> response = client.addComment(id, ownerId, request);
        log.info("POST /items/{}/comment on server finished for ownerId={}", id, ownerId);
        return response;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(ItemClient.USER_ID_HEADER) long ownerId,
                                         @RequestParam(name = "text") String text) {
        log.info("GET /items/search?text={} on server started for ownerId={}", text, ownerId);
        ResponseEntity<Object> response = client.search(text, ownerId);
        log.info("GET /items/search?text={} on server finished for ownerId={}", text, ownerId);
        return response;
    }
}
