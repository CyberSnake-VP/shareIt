package example.request.dto;

import example.item.dto.ItemResponse;

import java.time.LocalDateTime;
import java.util.List;

public record RequestResponseWithItems(
        Long id,
        String description,
        Long requestor,
        LocalDateTime created,
        List<ItemResponse> items) {
}
