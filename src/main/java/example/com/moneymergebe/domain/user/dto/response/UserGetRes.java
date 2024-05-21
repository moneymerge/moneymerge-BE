package example.com.moneymergebe.domain.user.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserGetRes {
    private Long userId;
    private String userName;
    private String userColor;

    public UserGetRes(Long userId, String name, String color) {
        this.userId = userId;
        this.userName = name;
        this.userColor = color;
    }
}