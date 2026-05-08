package example.item.dto;

import example.item.Comment;
import example.item.Item;
import example.user.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentResponse toCommentResponse(Comment comment) {
        ZoneId clientZone = ZoneId.of("Europe/Moscow");
        return new CommentResponse(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreatedDate().atZoneSameInstant(clientZone).toLocalDateTime()
        );
    }

    public static List<CommentResponse> toCommentResponse(Iterable<Comment> comments) {
        List<CommentResponse> response = new ArrayList<>();
        for (Comment comment : comments) {
            response.add(toCommentResponse(comment));
        }
        return response;
    }

    public static Comment toComment(CreateCommentRequest request, User author, Item item) {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setText(request.text());
        return comment;
    }
}
