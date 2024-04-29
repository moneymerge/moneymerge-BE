package example.com.moneymergebe.domain.record.dto.response;

import lombok.Getter;

@Getter
public class RecordGetLikeRes {
    private int likes;

    public RecordGetLikeRes(int likes) {
        this.likes = likes;
    }
}
