package example.com.moneymergebe.domain.user.dto.response;

import example.com.moneymergebe.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserGetRes {
    private Long userId;
    private String userName;
    //private String userColor; -> random?

    public UserGetRes(User user) {
        this.userId = user.getUserId();
        this.userName = user.getUsername();
    }
}