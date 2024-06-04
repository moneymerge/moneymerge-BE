package example.com.moneymergebe.domain.user.dto.response;

import example.com.moneymergebe.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserSearchRes {
    private Long userId;
    private String email;
    private String profileUrl;
    private String username;

    public UserSearchRes(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.profileUrl = user.getProfileUrl();
        this.username = user.getUsername();
    }
}
