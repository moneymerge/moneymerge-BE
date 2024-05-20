package example.com.moneymergebe.domain.book.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookStartDateReq {
    private Long bookId;
    private Long userId;

    @Min(value = 1, message = "유효한 숫자가 아닙니다.")
    @Max(value = 31, message = "유효한 숫자가 아닙니다.")
    private int startDate;
}
