package example.item;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ItemRepositoryImpl implements ItemRepository{
    private final Map<Long, List<Item>> items = new HashMap<>();
    AtomicLong generatorId = new AtomicLong(1);

    
    @Override
    public Item create(Long ownerId, Item item) {
        return null;
    }

    @Override
    public List<Item> getAll(Long ownerId) {
        return List.of();
    }

    @Override
    public Item getById(Long ownerId, Long itemId) {
        return null;
    }

    @Override
    public Item update(Long ownerId, Long itemId, Item item) {
        return null;
    }

}
