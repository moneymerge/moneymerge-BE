package example.com.moneymergebe.domain.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    PLUS_POINT("포인트가 적립되었습니다."),
    MINUS_POINT("포인트를 사용하였습니다."),
    BOOK_INVITED("가계부에 초대되었습니다."),
    BOOK_GET_COMMENT("가계부 기록에 댓글이 달렸습니다."),
    BOARD_GET_COMMENT("게시글에 댓글이 달렸습니다."),
    RECEIPT_ARRIVED("익명의 영수증이 도착했습니다.");

    private final String message;
}
