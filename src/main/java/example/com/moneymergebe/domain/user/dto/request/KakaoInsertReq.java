package example.com.moneymergebe.domain.user.dto.request;

import com.nimbusds.jose.shaded.gson.JsonElement;
import java.util.HashMap;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class KakaoInsertReq {
    private String email;
    private String username;
    private String profileUrl;
    private String accessToken;
    private String refreshToken;

    public static KakaoInsertReq of(JsonElement element, HashMap<String, String> tokens) {
        return KakaoInsertReq.builder()
            .email(
                element
                    .getAsJsonObject()
                    .get("kakao_account")
                    .getAsJsonObject()
                    .get("email")
                    .getAsString())
            .username(
                element
                    .getAsJsonObject()
                    .get("kakao_account")
                    .getAsJsonObject()
                    .get("profile")
                    .getAsJsonObject()
                    .get("nickname")
                    .getAsString())
            .profileUrl(
                element
                    .getAsJsonObject()
                    .get("kakao_account")
                    .getAsJsonObject()
                    .get("profile")
                    .getAsJsonObject()
                    .get("profile_image_url")
                    .getAsString())
            .accessToken(tokens.get("accessToken"))
            .refreshToken(tokens.get("refreshToken"))
            .build();
    }
}
