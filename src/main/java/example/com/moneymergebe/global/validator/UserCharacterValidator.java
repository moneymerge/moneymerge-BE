package example.com.moneymergebe.global.validator;

import static example.com.moneymergebe.global.response.ResultCode.ALREADY_USER_CHARACTER;
import static example.com.moneymergebe.global.response.ResultCode.NOT_USER_CHARACTER;

import example.com.moneymergebe.global.exception.GlobalException;

public class UserCharacterValidator {
    public static void checkCharacterToChange(boolean hasCharacter){
        if(!hasCharacter) throw new GlobalException(NOT_USER_CHARACTER);
    }

    public static void checkCharacterToBuy(boolean hasCharacter) {
        if(hasCharacter) throw new GlobalException(ALREADY_USER_CHARACTER);
    }

}
