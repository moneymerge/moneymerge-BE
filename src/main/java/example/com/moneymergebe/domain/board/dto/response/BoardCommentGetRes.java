package example.com.moneymergebe.domain.board.dto.response;

import example.com.moneymergebe.domain.board.entity.BoardComment;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardCommentGetRes {
    private Long boardCommentId;
    private Long userId;
    private String username;
    private String content;
    private String createdAt;
    private String modifiedAt;
    private int likes;

    public BoardCommentGetRes(BoardComment boardComment) {
        this.boardCommentId = boardComment.getBoardCommentId();
        this.userId = boardComment.getUser().getUserId();
        this.username = boardComment.getUser().getUsername();
        this.content = boardComment.getContent();
        this.createdAt = boardComment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.modifiedAt = boardComment.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.likes = boardComment.getLikes();
    }
}
