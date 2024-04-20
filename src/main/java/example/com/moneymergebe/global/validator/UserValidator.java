package example.com.moneymergebe.global.validator;

import static example.com.moneymergebe.global.response.ResultCode.*;

import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.global.exception.GlobalException;

public class UserValidator {
    public static void validate(User user){
        if(checkIsNull(user)) throw new GlobalException(NOT_FOUND_USER);
    }

    public static void checkUsername(User user) {
        if(!checkIsNull(user)) throw new GlobalException(DUPLICATED_USERNAME);
    }

    private static boolean checkIsNull(User user) {
        return user == null;
    }
}
