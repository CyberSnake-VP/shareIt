package example.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Long ownerId, Item item);
    List<Item> getAll(Long ownerId);
    Optional<Item> getById(Long ownerId, Long itemId);
}
