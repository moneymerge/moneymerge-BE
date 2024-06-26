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

    public static void checkUserPoints(User user, int price) {
        if(user.getPoints() < price) throw new GlobalException(NOT_ENOUGH_POINT);
    }

    public static void checkUser(User accessor, User author) {
        if(!accessor.getUserId().equals(author.getUserId())) throw new GlobalException(UNAUTHORIZED);
    }

    public static void checkTodayDrawStatus(User user) {
        if(user.isTodayDrawStatus()) throw new GlobalException(ALREADY_HAS_DRAWN);
    }

    private static boolean checkIsNull(User user) {
        return user == null;
    }
}
