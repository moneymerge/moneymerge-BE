package example.com.moneymergebe.domain.receipt.dto.request;

import lombok.Getter;

@Getter
public class SaveRandomReceiptReq {
    private Long pickReceiptId;

    public SaveRandomReceiptReq(Long pickReceiptId) {
        this.pickReceiptId = pickReceiptId;
    }
}
