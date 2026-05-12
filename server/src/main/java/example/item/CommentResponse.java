package example.item;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String text,
        String authorName,
        LocalDateTime created
) {
}
