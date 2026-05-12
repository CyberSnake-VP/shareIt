package example.item;

import java.util.List;

public interface ItemService {
    ItemResponse create(Long ownerId, CreateItemRequest request);

    List<ItemCommentResponse> getAll(Long ownerId);

    ItemCommentResponse getById(Long itemId);

    ItemResponse update(Long itemId, Long ownerId, UpdateItemRequest request);

    List<ItemResponse> search(Long ownerId, String text);

    CommentResponse addComment(Long itemId, Long authorId, CreateCommentRequest request);
}
