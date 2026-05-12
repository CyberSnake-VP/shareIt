package example.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateItemRequest(
        String name,
        String description,
        Boolean available,
        Long requestId
) {
}
