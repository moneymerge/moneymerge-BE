package example.com.moneymergebe.domain.board.dto.response;

import lombok.Getter;

@Getter
public class BoardCommentLikeRes {
    private int likes;

    public BoardCommentLikeRes(int likes) {
        this.likes = likes;
    }
}
