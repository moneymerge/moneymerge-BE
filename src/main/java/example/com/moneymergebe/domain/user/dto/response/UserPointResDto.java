package example.com.moneymergebe.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserPointResDto {
    private int points;

    public UserPointResDto(int points) {
        this.points = points;
    }
}
