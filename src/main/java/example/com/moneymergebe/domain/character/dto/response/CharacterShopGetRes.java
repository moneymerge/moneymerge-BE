package example.com.moneymergebe.domain.character.dto.response;

import example.com.moneymergebe.domain.character.entity.Character;
import lombok.Getter;

@Getter
public class CharacterShopGetRes {
    private Long characterId;
    private String name;
    private String image;
    private int points;
    private boolean possession;

    public CharacterShopGetRes(Character character, boolean possession) {
        this.characterId = character.getCharacterId();
        this.name = character.getName();
        this.image = character.getImage();
        this.points = character.getPoints();
        this.possession = possession;
    }
}
