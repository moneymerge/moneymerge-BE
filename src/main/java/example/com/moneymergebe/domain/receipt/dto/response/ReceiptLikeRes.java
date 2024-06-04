package example.com.moneymergebe.domain.receipt.dto.response;

import lombok.Getter;

@Getter
public class ReceiptLikeRes {
    private int likeCount;

    public ReceiptLikeRes(int likeCount) {
        this.likeCount = likeCount;
    }
}
