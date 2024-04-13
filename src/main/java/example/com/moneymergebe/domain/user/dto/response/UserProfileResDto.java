package example.com.moneymergebe.domain.user.dto.response;

import example.com.moneymergebe.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserProfileResDto {
    private Long userId;
    private String username;
    private String profileUrl;
    private Long point;
    private boolean alarm;

    public UserProfileResDto(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.profileUrl = user.getProfileUrl();
        this.point = user.getPoint();
        this.alarm = user.isAlarm();
    }
}
