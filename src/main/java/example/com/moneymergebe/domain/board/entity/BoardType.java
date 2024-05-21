package example.com.moneymergebe.domain.board.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardType {
    INFORMATION("정보나눔"),
    CERTIFICATION("인증"),
    FREE("자유");
    private final String value;
}
