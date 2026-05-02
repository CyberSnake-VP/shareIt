package example.item.dto;

import example.item.Comment;
import example.item.Item;
import example.user.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentResponse toCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated().toLocalDateTime()
        );
    }

    public static Comment toComment(CreateCommentRequest request, User author, Item item) {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setText(request.text());
        return comment;
    }
}
