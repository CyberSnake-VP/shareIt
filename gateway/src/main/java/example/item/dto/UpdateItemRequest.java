package example.item.dto;

public record UpdateItemRequest(
        String name,
        String description,
        Boolean available
) {
}
