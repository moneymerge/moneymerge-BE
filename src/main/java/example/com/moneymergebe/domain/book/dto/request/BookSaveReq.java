package example.com.moneymergebe.domain.book.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSaveReq {
    private Long userId; // 가계부 생성한 사용자 ID
    private String title;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "유효한 HEX 코드 형식이 아닙니다.")
    private String color;
    private Long yearGoal;
    private Long monthGoal;

    @Min(value = 1, message = "유효한 숫자가 아닙니다.")
    @Max(value = 31, message = "유효한 숫자가 아닙니다.")
    private int startDate;
    private Long[] userList; // 가계부 공유하는 사용자들 ID (가계부 생성한 사용자 포함)
}