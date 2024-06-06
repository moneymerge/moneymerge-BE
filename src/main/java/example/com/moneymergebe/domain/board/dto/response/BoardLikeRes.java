package example.com.moneymergebe.domain.board.dto.response;

import lombok.Getter;

@Getter
public class BoardLikeRes {
    private int likes;

    public BoardLikeRes(int likes) {
        this.likes = likes;
    }
}
