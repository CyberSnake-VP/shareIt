package example.request.dto;

import java.time.LocalDateTime;

public record RequestResponse (
        Long id,
        String description,
        Long requestor,
        LocalDateTime created
) {
}
