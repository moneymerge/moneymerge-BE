package example.com.moneymergebe.domain.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    ATTENDANCE_POINT_NOTIFICATION("출석", "포인트가 적립되었습니다."),
    GOAL_ACHIEVEMENT("목표", "목표를 달성했습니다."),
    GOAL_ACHIEVEMENT_FAILURE("목표", "목표를 달성하지 못했습니다."),
    RECEIPT_ARRIVED("하루 영수증", "익명의 영수증이 도착했습니다."),

    GOAL_CHANGE("목표", "목표를 수정하였습니다."),
    BOOK_INVITED("가계부", "가계부에 초대되었습니다."),
    BOOK_GET_COMMENT("가계부", "가계부 기록에 댓글이 달렸습니다."),
    BOARD_GET_COMMENT("커뮤니티", "게시글에 댓글이 달렸습니다.");

    private final String type;
    private final String message;
}
