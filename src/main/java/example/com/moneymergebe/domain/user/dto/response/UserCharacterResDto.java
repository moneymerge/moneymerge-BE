package example.com.moneymergebe.domain.user.dto.response;

import example.com.moneymergebe.domain.character.entity.Character;
import lombok.Getter;

@Getter
public class UserCharacterResDto {
    private Long characterId;
    private String name;
    private String image;

    public UserCharacterResDto(Character character) {
        this.characterId = character.getCharacterId();
        this.name = character.getName();
        this.image = character.getImage();
    }
}
