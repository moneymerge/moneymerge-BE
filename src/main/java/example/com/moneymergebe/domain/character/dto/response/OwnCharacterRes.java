package example.com.moneymergebe.domain.character.dto.response;

import lombok.Getter;

@Getter
public class OwnCharacterRes {
    private Long[] characterList;

    public OwnCharacterRes(Long[] characterList) {
        this.characterList = characterList;
    }
}
