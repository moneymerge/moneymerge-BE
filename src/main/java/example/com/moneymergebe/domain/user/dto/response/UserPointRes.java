package example.com.moneymergebe.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserPointRes {
    private int points;

    public UserPointRes(int points) {
        this.points = points;
    }
}
