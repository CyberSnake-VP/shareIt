package example.item;

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

    private static final String ITEM_NOT_CREATED = "Item not created";
    private static final String ITEM_NOT_FOUND = "Item not found";
    private static final String OWNER_NOT_FOUND = "Owner not found";

    @Override
    public ItemResponse create(Long ownerId, CreateItemRequest request) {
        log.info("Item add started: ownerId={}, itemName={}", ownerId, request.name());

        if (!userRepository.existById(ownerId)) {
            log.warn("Item add failed: owner not found, ownerId={}", ownerId);
            throw new NotFoundException(OWNER_NOT_FOUND);
        }

        Item item = itemRepository.create(ownerId, ItemMapper.toItem(request));

        if (item != null) {
            log.info("Item add completed: ownerId={}, itemId={}", ownerId, item.getId());
            return ItemMapper.toResponse(item);
        }
        throw new IllegalArgumentException(ITEM_NOT_CREATED);
    }

    @Override
    public List<ItemResponse> getAll(Long ownerId) {
        log.info("Get all items started: ownerId={}", ownerId);

        if (!userRepository.existById(ownerId)) {
            log.warn("Get all items failed: owner not found, ownerId={}", ownerId);
            throw new NotFoundException(OWNER_NOT_FOUND);
        }
        List<Item> items = itemRepository.getAll(ownerId);
        log.info("Get all items completed: ownerId={}, itemsCount={}", ownerId, items.size());
        return items.stream().map(ItemMapper::toResponse).toList();
    }

    @Override
    public ItemResponse getById(Long ownerId, Long itemId) {
        log.info("Item get by id started: ownerId={}, itemId={}", ownerId, itemId);

        if (!userRepository.existById(ownerId)) {
            log.warn("Get by id failed: owner not found, ownerId={}", ownerId);
            throw new NotFoundException(OWNER_NOT_FOUND);
        }

        Item item = itemRepository.getById(ownerId, itemId)
                .orElseThrow(() -> {
                    log.warn("Item get by id failed: item not found, itemId={}", itemId);
                    return new NotFoundException(ITEM_NOT_FOUND);
                });

        log.info("Item get by id completed: ownerId={}, itemId={}", ownerId, itemId);
        return ItemMapper.toResponse(item);
    }

    @Override
    public ItemResponse update(Long ownerId, Long itemId, UpdateItemRequest request) {
        log.info("Item update started: ownerId={}, itemId={}", ownerId, itemId);

        if (!userRepository.existById(ownerId)) {
            log.warn("Update failed: owner not found, ownerId={}", ownerId);
            throw new NotFoundException(OWNER_NOT_FOUND);
        }

        Item oldItem = itemRepository.getById(ownerId, itemId)
                .orElseThrow(() -> {
                    log.warn("Item update failed: item not found, itemId={}", itemId);
                    return new NotFoundException(ITEM_NOT_FOUND);
                });

        Item updatedItem = ItemMapper.toItemUpdate(request, oldItem);

        updatedItem = itemRepository.update(ownerId, itemId, updatedItem);

        log.info("Item update completed: ownerId={}, itemId={}", ownerId, itemId);
        return ItemMapper.toResponse(updatedItem);
    }

    @Override
    public List<ItemResponse> search(Long ownerId, String text) {
        log.info("Item search started: ownerId={}, text={}", ownerId, text);

        if (!userRepository.existById(ownerId)) {
            log.warn("Search failed: owner not found, ownerId={}", ownerId);
            throw new NotFoundException(OWNER_NOT_FOUND);
        }

        if (text == null || text.isBlank()) {
            log.debug("Item search: text is empty or null, text={}", text);
            return Collections.emptyList();
        }

        List<Item> allOwnerItems = itemRepository.getAll(ownerId);
        if (allOwnerItems.isEmpty()) {
            return Collections.emptyList();
        }

        log.debug("Item search: checking text={}", text);
        List<Item> findItems = allOwnerItems.stream()
                .filter(item ->
                        ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                                item.isAvailable())
                )
                .toList();

        log.info("Item search completed: ownerId={}, text={}, itemsCount={}", ownerId, text, findItems.size());
        return findItems.stream().map(ItemMapper::toResponse).toList();
    }

}
