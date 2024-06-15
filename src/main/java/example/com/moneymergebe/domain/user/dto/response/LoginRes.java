package example.com.moneymergebe.domain.user.dto.response;

import lombok.Getter;

@Getter
public class LoginRes {
    private String accessToken;
    private String refreshToken;

    public LoginRes(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
