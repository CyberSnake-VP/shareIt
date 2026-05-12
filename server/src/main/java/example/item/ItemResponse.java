package example.item;

public record ItemResponse(
        Long id,
        String name,
        Long owner,
        String description,
        boolean available
) {
}
