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
    LOG_IN_REQUIRED(HttpStatus.UNAUTHORIZED, 1004, "다시 로그인 해주세요."),
    SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1005, "서버 시스템 문제가 발생했습니다."),
    NOT_FOUND_FILE(HttpStatus.NOT_FOUND, 1006, "파일을 찾을 수 없습니다."),
    MAXIMUM_UPLOAD_FILE_SIZE(HttpStatus.BAD_REQUEST, 1007, "10MB를 초과할 수 없습니다."),
    INVALID_IMAGE_FILE(HttpStatus.BAD_REQUEST, 1008, "지원하지 않는 이미지 파일 형식입니다."),

    // 사용자 2000번대
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, 2000, "사용자를 찾을 수 없습니다."),
    DUPLICATED_USERNAME(HttpStatus.CONFLICT, 2001, "중복된 닉네임입니다."),
    NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, 2002, "포인트가 부족합니다."),

    // 가계부 3000번대
    NOT_FOUND_BOOK(HttpStatus.NOT_FOUND, 3000, "가계부를 찾을 수 없습니다."),
    NOT_BOOK_MEMBER(HttpStatus.BAD_REQUEST, 3001, "입력한 가계부의 멤버가 아닙니다."),

    // 캐릭터 4000번대
    NOT_FOUND_CHARACTER(HttpStatus.NOT_FOUND, 4000, "캐릭터를 찾을 수 없습니다."),
    NOT_USER_CHARACTER(HttpStatus.BAD_REQUEST, 4001, "사용자가 소유하고 있는 캐릭터가 아닙니다."),
    ALREADY_USER_CHARACTER(HttpStatus.BAD_REQUEST, 4002, "사용자가 이미 소유하고 있는 캐릭터입니다.");

    private final HttpStatus status;
    private final int code;
    private final String message;
}
