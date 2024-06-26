package example.com.moneymergebe.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResultCode {
    // 글로벌 1000번대
    SUCCESS(HttpStatus.OK, 0, "정상 처리 되었습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, 1000, "잘못된 입력값입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 1001, "권한이 없는 사용자입니다."),
    REFRESH_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, 1002, "Refresh Token이 필요합니다."),
    LOG_IN_REQUIRED(HttpStatus.UNAUTHORIZED, 1003, "다시 로그인 해주세요."),
    SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1004, "서버 시스템 문제가 발생했습니다."),
    NOT_FOUND_FILE(HttpStatus.NOT_FOUND, 1005, "파일을 찾을 수 없습니다."),
    MAXIMUM_UPLOAD_FILE_SIZE(HttpStatus.BAD_REQUEST, 1006, "10MB를 초과할 수 없습니다."),
    INVALID_IMAGE_FILE(HttpStatus.BAD_REQUEST, 1007, "지원하지 않는 이미지 파일 형식입니다."),

    // 사용자 2000번대
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, 2000, "사용자를 찾을 수 없습니다."),
    DUPLICATED_USERNAME(HttpStatus.CONFLICT, 2001, "중복된 닉네임입니다."),
    NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, 2002, "포인트가 부족합니다."),

    // 가계부 3000번대
    NOT_FOUND_BOOK(HttpStatus.NOT_FOUND, 3000, "가계부를 찾을 수 없습니다."),
    NOT_BOOK_MEMBER(HttpStatus.BAD_REQUEST, 3001, "입력한 가계부의 멤버가 아닙니다."),
    EXISTING_BOOK_MEMBER(HttpStatus.BAD_REQUEST, 3002, "이미 가계부에 참여중인 멤버입니다."),
    DELETE_NOT_AGREED(HttpStatus.BAD_REQUEST, 3003, "가계부 삭제 동의가 필요합니다."),

    // 캐릭터 4000번대
    NOT_FOUND_CHARACTER(HttpStatus.NOT_FOUND, 4000, "캐릭터를 찾을 수 없습니다."),
    NOT_USER_CHARACTER(HttpStatus.BAD_REQUEST, 4001, "사용자가 소유하고 있는 캐릭터가 아닙니다."),
    ALREADY_USER_CHARACTER(HttpStatus.BAD_REQUEST, 4002, "사용자가 이미 소유하고 있는 캐릭터입니다."),

    // 레코드 5000번대
    NOT_FOUND_RECORD(HttpStatus.NOT_FOUND, 5000, "레코드를 찾을 수 없습니다."),
    NOT_BOOK_RECORD(HttpStatus.BAD_REQUEST, 5001, "가계부의 레코드가 아닙니다."),

    // 댓글 6000번대
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, 6000, "댓글을 찾을 수 없습니다."),
    UNMATCHED_RECORD_COMMENT(HttpStatus.BAD_REQUEST, 6001, "해당 레코드의 댓글이 아닙니다."),

    // 영수증 7000번대
    NOT_FOUND_RECEIPT(HttpStatus.NOT_FOUND, 7000, "영수증을 찾을 수 없습니다."),
    ALREADY_HAS_DRAWN(HttpStatus.BAD_REQUEST, 7001, "하루 당 한 번만 뽑기 가능합니다."),
    ALREADY_WRITTEN_RECEIPT_DATE(HttpStatus.CONFLICT, 7002, "하루 당 한 개만 작성 가능합니다."),

    // 게시글 8000번대
    NOT_FOUND_BOARD(HttpStatus.NOT_FOUND, 8000, "게시글을 찾을 수 없습니다."),
    NOT_FOUND_RANGE(HttpStatus.BAD_REQUEST, 8001, "유효한 범위가 아닙니다."),

    // 게시글 댓글 9000번대
    NOT_FOUND_BOARD_COMMENT(HttpStatus.NOT_FOUND, 9000, "게시글 댓글을 찾을 수 없습니다."),
    UNMATCHED_BOARD_COMMENT(HttpStatus.BAD_REQUEST, 9001, "해당 게시글의 댓글이 아닙니다."),

    // 카테고리 10000번대
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, 10000, "카테고리를 찾을 수 없습니다."),

    // 알림 11000번대
    NOT_FOUND_NOTIFICATION(HttpStatus.NOT_FOUND, 11000, "알림을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final int code;
    private final String message;
}
