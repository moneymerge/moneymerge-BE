package example.com.moneymergebe.domain.receipt.dto.response;

import java.util.List;
import lombok.Getter;

@Getter
public class ReceiptAnalysisRes {
    private List<ReceiptMonthAnalysisRes> months;

    public ReceiptAnalysisRes(List<ReceiptMonthAnalysisRes> months) {
        this.months = months;
    }
}
