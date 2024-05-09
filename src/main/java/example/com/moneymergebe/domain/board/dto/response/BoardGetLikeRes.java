package example.com.moneymergebe.domain.board.dto.response;

import lombok.Getter;

@Getter
public class BoardGetLikeRes {
    private int likes;

    public BoardGetLikeRes(int likes) {
        this.likes = likes;
    }
}
