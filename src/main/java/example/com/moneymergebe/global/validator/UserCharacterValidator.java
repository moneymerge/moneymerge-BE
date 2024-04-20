package example.com.moneymergebe.global.validator;

import static example.com.moneymergebe.global.response.ResultCode.NOT_USER_CHARACTER;

import example.com.moneymergebe.domain.user.entity.UserCharacter;
import example.com.moneymergebe.global.exception.GlobalException;

public class UserCharacterValidator {
    public static void validate(UserCharacter userCharacter){
        if(checkIsNull(userCharacter)) throw new GlobalException(NOT_USER_CHARACTER);
    }

    private static boolean checkIsNull(UserCharacter userCharacter) {
        return userCharacter == null;
    }

}
