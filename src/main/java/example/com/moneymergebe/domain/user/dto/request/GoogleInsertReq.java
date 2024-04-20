package example.com.moneymergebe.domain.user.dto.request;

import com.nimbusds.jose.shaded.gson.JsonElement;
import java.util.HashMap;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GoogleInsertReq {
    private String email;
    private String username;
    private String profileUrl;
    private String accessToken;
    private String refreshToken;

    public static GoogleInsertReq of(JsonElement element, HashMap<String, String> tokens) {
        return GoogleInsertReq.builder()
            .email(
                element
                    .getAsJsonObject()
                    .get("profile")
                    .getAsJsonObject()
                    .get("email")
                    .getAsString())
            .username(
                element
                    .getAsJsonObject()
                    .get("profile")
                    .getAsJsonObject()
                    .get("name")
                    .getAsString())
            .profileUrl(
                element
                    .getAsJsonObject()
                    .get("profile")
                    .getAsJsonObject()
                    .get("picture")
                    .getAsString())
            .accessToken(tokens.get("accessToken"))
            .refreshToken(tokens.get("refreshToken"))
            .build();
    }
}
