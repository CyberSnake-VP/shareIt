package example.item;

import example.item.dto.CreateItemRequest;
import example.item.dto.ItemResponse;
import example.item.dto.UpdateItemRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class ItemServiceImpl implements ItemService{
    private final ItemRepository itemRepository;



    @Override
    public ItemResponse create(Long ownerId, CreateItemRequest request) {
        return null;
    }

    @Override
    public List<ItemResponse> getAll(Long ownerId) {
        return List.of();
    }

    @Override
    public ItemResponse getById(Long ownerId, Long itemId) {
        return null;
    }

    @Override
    public ItemResponse update(Long ownerId, Long itemId, UpdateItemRequest request) {
        return null;
    }

    @Override
    public List<ItemResponse> search(Long ownerId, String text) {
        return List.of();
    }
}
