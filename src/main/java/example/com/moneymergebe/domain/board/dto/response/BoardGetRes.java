package example.com.moneymergebe.domain.board.dto.response;

import example.com.moneymergebe.domain.board.entity.Board;
import example.com.moneymergebe.domain.board.entity.BoardType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class BoardGetRes {
    private Long boardId;
    private BoardType boardType;
    private String title;
    private String content;
    private Boolean isImage;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int likes;

    private List<BoardCommentGetRes> commentGetResList;

    public BoardGetRes(Board board, List<BoardCommentGetRes> commentGetResList, int likes) {
        this.boardId = board.getBoardId();
        this.boardType = board.getBoardType();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.isImage =  board.getImage() == null ? false : true;
        this.userId = board.getUser().getUserId();
        this.username = board.getUser().getUsername();
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
        this.likes = likes;
        this.commentGetResList = commentGetResList;

    }
}