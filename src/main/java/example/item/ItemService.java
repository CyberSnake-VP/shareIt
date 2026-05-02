package example.item;

import example.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemResponse create(Long ownerId, CreateItemRequest request);

    List<ItemResponse> getAll(Long ownerId);

    ItemResponse getById(Long itemId, Long ownerId);

    ItemResponse update(Long itemId, Long ownerId, UpdateItemRequest request);

    List<ItemResponse> search(Long ownerId, String text);

    CommentResponse addComment(Long itemId, Long authorId, CreateCommentRequest request);
}
