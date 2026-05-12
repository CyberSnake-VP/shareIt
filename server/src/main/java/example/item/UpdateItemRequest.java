package example.item;

public record UpdateItemRequest(
        String name,
        String description,
        Boolean available
) {
    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    public boolean hasAvailable() {
        return available != null;
    }
}
