package example.com.moneymergebe.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserPointResDto {
    private Long point;

    public UserPointResDto(Long point) {
        this.point = point;
    }
}
