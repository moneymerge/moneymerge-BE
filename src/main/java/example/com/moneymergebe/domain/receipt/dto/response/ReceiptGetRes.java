package example.com.moneymergebe.domain.receipt.dto.response;

import example.com.moneymergebe.domain.receipt.entity.Receipt;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class ReceiptGetRes {
    private Long receiptId;
    private LocalDate date;
    private String content;
    private boolean shared;
    private int positive;
    private int negative;
    private int likeCount;

    public ReceiptGetRes(Receipt receipt, int likeCount) {
        this.receiptId = receipt.getReceiptId();
        this.date = receipt.getDate();
        this.content = receipt.getContent();
        this.shared = receipt.isShared();
        this.positive = receipt.getPositive();
        this.negative = receipt.getNegative();
        this.likeCount = likeCount;
    }
}
