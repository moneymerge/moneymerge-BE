package example.com.moneymergebe.domain.receipt.dto.response;

import java.util.List;
import lombok.Getter;

@Getter
public class ReceiptGetMonthRes {
    private int totalPositive;
    private int totalNegative;
    private Long receivedReceiptId;
    List<ReceiptGetRes> receiptList;

    public ReceiptGetMonthRes(int totalPositive, int totalNegative, Long receivedReceiptId,
        List<ReceiptGetRes> receiptList) {
        this.totalPositive = totalPositive;
        this.totalNegative = totalNegative;
        this.receivedReceiptId = receivedReceiptId;
        this.receiptList = receiptList;
    }
}
