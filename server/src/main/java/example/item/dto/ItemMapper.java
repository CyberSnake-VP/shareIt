package example.item.dto;

import example.booking.dto.BookingResponse;
import example.item.Comment;
import example.item.Item;
import example.user.User;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        item.setRequestId(request.requestId());
        return item;
    }

    public static List<ItemResponse> toResponses(Iterable<Item> items) {
        List<ItemResponse> responses = new ArrayList<>();
        for (Item item : items) {
            responses.add(toResponse(item));
        }
        return responses;
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

    public static List<ItemCommentResponse> toItemCommentResponse(List<Item> items,
                                                                  Map<Long, List<Comment>> commentMap,
                                                                  Map<Long, BookingResponse> lastBookingMap,
                                                                  Map<Long, BookingResponse> nexBookingMap) {
        // начинаю перебирать items
        return items.stream()
                .map(item -> {
                    // получаю id item
                    Long id = item.getId();
                    // получаю список его комментов и значение из других map
                    List<Comment> comments = commentMap.getOrDefault(id, Collections.emptyList());
                    BookingResponse lastBooking = lastBookingMap.get(id);
                    BookingResponse nextBooking = nexBookingMap.get(id);
                    // возвращаю mapping в нужный объект и собираю в to list результат
                    return toItemCommentResponse(item, comments, lastBooking, nextBooking);
                }).toList();
    }


    public static ItemCommentResponse toItemCommentResponse(Item item,
                                                            List<Comment> comments,
                                                            BookingResponse lastBooking,
                                                            BookingResponse nextBooking) {
        return new ItemCommentResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                lastBooking,
                nextBooking,
                CommentMapper.toCommentResponse(comments)
        );
    }


}
