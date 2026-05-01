package example.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import example.exception.NotFoundException;
import example.item.dto.CreateItemRequest;
import example.item.dto.ItemMapper;
import example.item.dto.ItemResponse;
import example.item.dto.UpdateItemRequest;
import example.user.User;
import example.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private static final String ITEM_NOT_FOUND = "Item not found";
    private static final String OWNER_NOT_FOUND = "Owner not found";


    @Override
    public ItemResponse create(Long ownerId, CreateItemRequest request) {
        log.info("Item add started: ownerId={}, itemName={}", ownerId, request.name());

        log.debug("Item add: checking ownerId={}", ownerId);
        User owner = getOwnerOrThrow(ownerId);

        Item item = ItemMapper.toItem(request, owner);
        Item saved = itemRepository.save(item);

        log.info("Item add completed: ownerId={}, itemId={}", ownerId, saved.getId());
        return ItemMapper.toResponse(saved);
    }

    @Override
    public List<ItemResponse> getAll(Long ownerId) {
        log.info("Get all items started: ownerId={}", ownerId);

        log.debug("Get all items by id: checking ownerId={}", ownerId);
        getOwnerOrThrow(ownerId);

        List<Item> items = itemRepository.findAllByOwnerId(ownerId);

        if (items.isEmpty()) {
            log.info("Not found items for ownerId={}", ownerId);
        }

        log.info("Get all items completed: ownerId={}, itemsCount={}", ownerId, items.size());
        return ItemMapper.toResponses(items);
    }

    @Override
    public ItemResponse getById(Long itemId, Long ownerId) {
        log.info("Get by id items started: ownerId={}, itemId={}", ownerId, itemId);

        log.debug("Get by id items: checking ownerId={}", ownerId);
        getOwnerOrThrow(ownerId);

        Item item = getItemByIdAndOwnerIdOrThrow(itemId, ownerId);

        log.info("Get by id items completed: ownerId={}, itemId={}", ownerId, itemId);
        return ItemMapper.toResponse(item);
    }

    @Override
    public ItemResponse update(Long ownerId, Long itemId, UpdateItemRequest request) {
        log.info("Update item started: itemId={}, ownerId={}", itemId, ownerId);

        log.debug("Update item: checking ownerId={}", ownerId);
        getOwnerOrThrow(ownerId);

        log.debug("Update item: checking itemId={}, ownerId={}", itemId, ownerId);
        Item existingItem = getItemByIdAndOwnerIdOrThrow(itemId, ownerId);

        Item updatedItem = ItemMapper.toItemUpdate(request, existingItem);
        itemRepository.save(updatedItem);

        log.info("Update item completed: itemId={}, ownerId={}", itemId, ownerId);
        return ItemMapper.toResponse(updatedItem);
    }

    @Override
    public List<ItemResponse> search(Long ownerId, String text) {
        log.info("Search items started: ownerId={}, text={}", ownerId, text);

        if (text == null || text.isBlank()) {
            log.debug("Item search: text is empty or null, text={}", text);
            return Collections.emptyList();
        }

        log.debug("Search items: checking ownerId={}", ownerId);
        getOwnerOrThrow(ownerId);

        BooleanExpression expression = QItem.item.owner.id.eq(ownerId)
                .and(QItem.item.name.containsIgnoreCase(text)
                        .or(QItem.item.description.containsIgnoreCase(text)));

        Iterable<Item> foundItems = itemRepository.findAll(expression);

        log.info("Search items completed");
        return ItemMapper.toResponses(foundItems);
    }

    private User getOwnerOrThrow(Long ownerId) {
        return userRepository.findById(ownerId)
                .orElseThrow(() -> {
                    log.warn("Get all items failed: owner not found, ownerId={}", ownerId);
                    return new NotFoundException(OWNER_NOT_FOUND);
                });
    }

    private Item getItemByIdAndOwnerIdOrThrow(Long itemId, Long ownerId) {
        return itemRepository.findByIdAndOwnerId(itemId, ownerId)
                .orElseThrow(() -> {
                    log.warn("Item get by id failed: item not found, itemId={}", itemId);
                    return new NotFoundException(ITEM_NOT_FOUND);
                });
    }



}
