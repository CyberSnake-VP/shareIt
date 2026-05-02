package example.item.dto;

import java.time.LocalDateTime;

public record CommentResponse (
    Long id,
    String text,
    String authorName,
    LocalDateTime created
){
}
