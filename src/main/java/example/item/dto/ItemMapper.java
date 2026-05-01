package example.item.dto;

import example.item.Item;
import example.user.User;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemResponse toResponse(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getOwner().getId(),
                item.getDescription(),
                item.isAvailable());
    }

    public static Item toItem(CreateItemRequest request, User owner) {
        Item item = new Item();
        item.setName(request.name());
        item.setDescription(request.description());
        item.setAvailable(request.available());
        item.setOwner(owner);
        return item;
    }

    public static Item toItemUpdate(UpdateItemRequest request, Item item) {

        if (request.hasName()) {
            item.setName(request.name());
        }
        if (request.hasDescription()) {
            item.setDescription(request.description());
        }
        if (request.hasAvailable()) {
            item.setAvailable(request.available());
        }

        return item;
    }

}
