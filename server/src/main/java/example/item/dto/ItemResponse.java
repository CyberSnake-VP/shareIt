package example.item.dto;

public record ItemResponse(
        Long id,
        String name,
        Long owner,
        String description,
        boolean available
) {
}
