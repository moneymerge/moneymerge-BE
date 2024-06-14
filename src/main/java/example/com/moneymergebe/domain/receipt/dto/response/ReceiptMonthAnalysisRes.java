package example.com.moneymergebe.domain.receipt.dto.response;

import lombok.Getter;

@Getter
public class ReceiptMonthAnalysisRes {
    private int month;
    private int positiveSum;
    private int negativeSum;

    public ReceiptMonthAnalysisRes(int month, int positiveSum, int negativeSum) {
        this.month = month;
        this.positiveSum = positiveSum;
        this.negativeSum = negativeSum;
    }
}
