package example.com.moneymergebe.domain.book.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookUserColorReq {
    private Long bookId;
    private Long userId;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "유효한 HEX 코드 형식이 아닙니다.")
    private String userColor;

}
