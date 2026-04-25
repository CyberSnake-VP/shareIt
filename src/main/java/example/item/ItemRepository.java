package example.item;

import java.util.List;

public interface ItemRepository {
    Item create(Long ownerId, Item item);
    List<Item> getAll(Long ownerId);
    Item getById(Long ownerId, Long itemId);
    Item update(Long ownerId, Long itemId, Item item);
}
