package example.com.moneymergebe.domain.character.dto.response;

import example.com.moneymergebe.domain.user.entity.UserCharacter;
import lombok.Getter;

@Getter
public class CharacterGetRes {
    private Long characterId;
    private String name;
    private String image;

    public CharacterGetRes(UserCharacter userCharacter) {
        this.characterId = userCharacter.getCharacter().getCharacterId();
        this.name = userCharacter.getCharacter().getName();
        this.image = userCharacter.getCharacter().getImage();
    }
}
