package example.com.moneymergebe.domain.board.dto.response;

import example.com.moneymergebe.domain.board.entity.BoardComment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardCommentGetRes {
    private Long boardCommentId;
    private Long userId;
    private String username;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int likes;

    public BoardCommentGetRes(BoardComment boardComment) {
        this.boardCommentId = boardComment.getBoardCommentId();
        this.userId = boardComment.getUser().getUserId();
        this.username = boardComment.getUser().getUsername();
        this.content = boardComment.getContent();
        this.createdAt = boardComment.getCreatedAt();
        this.modifiedAt = boardComment.getModifiedAt();
        this.likes = boardComment.getLikes();
    }
}
