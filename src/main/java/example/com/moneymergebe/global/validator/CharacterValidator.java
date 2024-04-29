package example.com.moneymergebe.global.validator;

import static example.com.moneymergebe.global.response.ResultCode.NOT_FOUND_CHARACTER;

import example.com.moneymergebe.domain.character.entity.Character;
import example.com.moneymergebe.global.exception.GlobalException;

public class CharacterValidator {
    public static void validate(Character character){
        if(checkIsNull(character)) throw new GlobalException(NOT_FOUND_CHARACTER);
    }

    private static boolean checkIsNull(Character character) {
        return character == null;
    }
}
