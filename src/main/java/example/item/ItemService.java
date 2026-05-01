package example.item;

import example.item.dto.CreateItemRequest;
import example.item.dto.ItemResponse;
import example.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {
    ItemResponse create(Long ownerId, CreateItemRequest request);

    List<ItemResponse> getAll(Long ownerId);

    ItemResponse getById(Long itemId, Long ownerId);

    ItemResponse update(Long itemId, Long ownerId, UpdateItemRequest request);

    List<ItemResponse> search(Long ownerId, String text);
}
