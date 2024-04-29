package example.com.moneymergebe.domain.record.dto.response;

import lombok.Getter;

@Getter
public class RecordGetDislikeRes {
    private int dislikes;

    public RecordGetDislikeRes(int dislikes) {
        this.dislikes = dislikes;
    }
}
