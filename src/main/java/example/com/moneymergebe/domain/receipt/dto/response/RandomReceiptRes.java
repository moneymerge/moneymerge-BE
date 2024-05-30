package example.com.moneymergebe.domain.receipt.dto.response;

import lombok.Getter;

@Getter
public class RandomReceiptRes {
    private Long[] receiptIdList;

    public RandomReceiptRes(Long[] receiptIdList) {
        this.receiptIdList = receiptIdList;
    }
}
