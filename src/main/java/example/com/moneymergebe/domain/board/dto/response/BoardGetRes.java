package example.com.moneymergebe.domain.board.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import example.com.moneymergebe.domain.board.entity.Board;
import example.com.moneymergebe.domain.board.entity.BoardType;
import java.time.format.DateTimeFormatter;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class BoardGetRes {
    private Long boardId;
    private String boardType;
    private String title;
    private String content;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String image;
    private Long userId;
    private String author;
    private String createdAt;
    private String modifiedAt;
    private int likes;
    private int comments;

    public BoardGetRes(Board board, int comments) {
        this.boardId = board.getBoardId();
        this.boardType = board.getBoardType().getValue();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.image =  board.getImage();
        this.userId = board.getUser().getUserId();
        this.author = board.getUser().getUsername();
        this.createdAt = board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.modifiedAt = board.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.likes = board.getLikes();
        this.comments = comments;
    }
}
