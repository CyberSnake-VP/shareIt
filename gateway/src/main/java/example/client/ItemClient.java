package example.client;

import example.item.dto.CreateCommentRequest;
import example.item.dto.CreateItemRequest;
import example.item.dto.UpdateItemRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String url,
                      RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(url + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(long userId, CreateItemRequest body) {
        return post("", userId, body);
    }

    public ResponseEntity<Object> updateItem(long itemId, long userId, UpdateItemRequest body) {
        return patch("/" + itemId, userId, body);
    }

    public ResponseEntity<Object> getById(long itemId, long ownerId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> getAllByOwner(long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> search(String text, long userId) {
        Map<String, Object> params = Map.of("text", text);
        return get("/search?text={text}", userId, params);
    }

    public ResponseEntity<Object> addComment(long itemId, long userId, CreateCommentRequest body) {
        Map<String, Object> params = Map.of("id", itemId);
        return post("/{id}/comment", userId, params, body);
    }

}
