package example.com.moneymergebe.domain.user.dto.response;

import example.com.moneymergebe.domain.character.entity.Character;
import example.com.moneymergebe.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserInfoRes {
    private Long userId;
    private String username;
    private String profileUrl;
    private Long characterId;
    private String characterName;
    private String characterImage;

    public UserInfoRes(User user, Character character) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.profileUrl = user.getProfileUrl();
        this.characterId = character.getCharacterId();
        this.characterName = character.getName();
        this.characterImage = character.getImage();
    }
}
