package example.com.moneymergebe.domain.user.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserNameReqDto {
    private Long userId;

    @Size(min = 1, max = 10, message = "1 ~ 10 글자로 입력해주세요.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]*$", message = "특수문자는 사용할 수 없습니다.")
    private String username;
}
