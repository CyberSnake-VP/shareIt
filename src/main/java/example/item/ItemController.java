package example.item;

import example.item.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse create(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                               @RequestBody @Valid CreateItemRequest request) {
        log.info("POST /items started: ownerId={}, itemName={}", ownerId, request.name());
        ItemResponse response = itemService.create(ownerId, request);
        log.info("POST /items completed: ownerId={}, itemId={}", ownerId, response.id());
        return response;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponse> getAll(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("GET /items started: ownerId={}", ownerId);
        List<ItemResponse> responses = itemService.getAll(ownerId);
        log.info("GET /items completed: ownerId={}, items={}", ownerId, responses.size());
        return responses;
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponse getById(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                @PathVariable Long itemId) {
        log.info("GET /items/{} started: ownerId={}", itemId, ownerId);
        ItemResponse response = itemService.getById(itemId, ownerId);
        log.info("GET /items/{} completed: ownerId={}, itemId={}", itemId, ownerId, response.id());
        return response;
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponse update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                               @PathVariable Long itemId,
                               @RequestBody @Valid UpdateItemRequest request) {
        log.info("PATCH /items/{} started: ownerId={}", itemId, ownerId);
        ItemResponse response = itemService.update(itemId, ownerId, request);
        log.info("PATCH /items/{} completed: ownerId={}, itemId={}", itemId, ownerId, response.id());
        return response;
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponse> search(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                     @RequestParam("text") String text) {
        log.info("GET /items/search started: ownerId={}, text={}", ownerId, text);
        List<ItemResponse> responses = itemService.search(ownerId, text);
        log.info("GET /items/search completed: ownerId={}, text={}, count={}", ownerId, text, responses.size());
        return responses;
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(@PathVariable Long itemId,
                                      @RequestHeader("X-Sharer-User-Id") Long authorId,
                                      @RequestBody @Valid CreateCommentRequest request) {
        log.info("POST /items/{}/comment started: authorId={}", itemId, authorId);
        CommentResponse response = itemService.addComment(itemId, authorId, request);
        log.info("POST /items/{}/comment completed: authorId={}", itemId, authorId);
        return response;
    }
}