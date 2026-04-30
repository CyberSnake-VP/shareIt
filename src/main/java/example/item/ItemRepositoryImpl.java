package example.item;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, List<Item>> items = new HashMap<>();
    private final AtomicLong generatorId = new AtomicLong(1);


    @Override
    public Item create(Long ownerId, Item item) {
        if (ownerId == null || item == null) {
            return null;
        }
        item.setId(generatorId.getAndIncrement());
        item.setOwner(ownerId);
        items.computeIfAbsent(ownerId, k -> new ArrayList<>()).add(item);
        return item;
    }

    @Override
    public List<Item> getAll(Long ownerId) {
        if (ownerId == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(items.getOrDefault(ownerId, new ArrayList<>()));
    }

    @Override
    public Optional<Item> getById(Long ownerId, Long itemId) {
        if (ownerId == null || itemId == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(items.get(ownerId))
                .flatMap(l -> l.stream()
                        .filter(i -> i.getId().equals(itemId))
                        .findFirst());
    }

    @Override
    public Item update(Long ownerId, Long itemId, Item item) {
        List<Item> ownerItems = items.get(ownerId);
        if (ownerItems == null) {
            return null;
        }
        for (int i = 0; i < ownerItems.size(); i++) {
            if (ownerItems.get(i).getId().equals(itemId)) {
                ownerItems.set(i, item);
                return item;
            }
        }
        return null;
    }
}
