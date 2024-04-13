package example.com.moneymergebe.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResultCode {
    // 글로벌 1000번대
    SUCCESS(HttpStatus.OK, 1000, "정상 처리 되었습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 1001, "권한이 없는 사용자입니다."),
    REFRESH_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, 1002, "Refresh Token이 필요햡니다."),
    LOG_IN_REQUIRED(HttpStatus.UNAUTHORIZED, 1004, "다시 로그인 해주세요."),

    // 사용자 2000번대
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, 2000, "사용자를 찾을 수 없습니다."),

    // 가계부 3000번대
    NOT_FOUND_BOOK(HttpStatus.NOT_FOUND, 3000, "가계부를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final int code;
    private final String message;
}
