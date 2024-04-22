package example.com.moneymergebe.global.validator;

import static example.com.moneymergebe.global.response.ResultCode.*;

import example.com.moneymergebe.domain.record.entity.Record;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.global.exception.GlobalException;

public class RecordValidator {
    public static void validate(Record record){
        if(checkIsNull(record)) throw new GlobalException(NOT_FOUND_RECORD);
    }

    public static void checkUser(User accessor, User author) {
        if(!accessor.getUserId().equals(author.getUserId())) throw new GlobalException(UNAUTHORIZED);
    }

    private static boolean checkIsNull(Record record) {
        return record == null;
    }
}
