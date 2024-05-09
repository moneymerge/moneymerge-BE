package example.com.moneymergebe.domain.board.dto.response;

import lombok.Getter;

@Getter
public class BoardCommentGetLikeRes {
    private int likes;

    public BoardCommentGetLikeRes(int likes) {
        this.likes = likes;
    }
}
