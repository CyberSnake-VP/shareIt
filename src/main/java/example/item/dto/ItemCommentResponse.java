package example.item.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ItemCommentResponse(
        Long id,
        String name,
        String description,
        Boolean available,
        LocalDateTime lastBooking,
        LocalDateTime nextBooking,
        List<CommentResponse> comments
) {
}
