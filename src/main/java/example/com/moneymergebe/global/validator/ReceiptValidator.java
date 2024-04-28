package example.com.moneymergebe.global.validator;

import static example.com.moneymergebe.global.response.ResultCode.NOT_FOUND_RECEIPT;
import static example.com.moneymergebe.global.response.ResultCode.UNAUTHORIZED;

import example.com.moneymergebe.domain.receipt.entity.Receipt;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.global.exception.GlobalException;

public class ReceiptValidator {
    public static void validate(Receipt receipt){
        if(checkIsNull(receipt)) throw new GlobalException(NOT_FOUND_RECEIPT);
    }

    public static void checkAuthority(User accessor, User author) {
        if(!accessor.getUserId().equals(author.getUserId())) throw new GlobalException(UNAUTHORIZED);
    }

    public static void checkAuthority(User accessor, Receipt receipt) { // 나의 영수증이거나 공유 받은 영수증인지 검사
        Long receivedReceiptId = accessor.getReceivedReceiptId();
        if(!(accessor.getUserId().equals(receipt.getUser().getUserId()) || (receivedReceiptId != null && receivedReceiptId.equals(receipt.getReceiptId())))) throw new GlobalException(UNAUTHORIZED);
    }

    public static void checkLikeAuthority(User accessor, Receipt receipt) { // 공유 받은 영수증인지 검사
        Long receivedReceiptId = accessor.getReceivedReceiptId();
        if(receivedReceiptId == null || !receivedReceiptId.equals(receipt.getReceiptId())) throw new GlobalException(UNAUTHORIZED);
    }

    private static boolean checkIsNull(Receipt receipt) {
        return receipt == null;
    }
}
