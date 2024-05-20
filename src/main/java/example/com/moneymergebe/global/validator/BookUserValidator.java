package example.com.moneymergebe.global.validator;

import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.global.exception.GlobalException;

import static example.com.moneymergebe.global.response.ResultCode.*;

public class BookUserValidator {
    public static void checkMember(BookUser bookUser){
        if(checkIsNull(bookUser)) throw new GlobalException(NOT_BOOK_MEMBER);
    }

    public static void newMember(BookUser bookUser){
        if(!checkIsNull(bookUser)) throw new GlobalException(EXISTING_BOOK_MEMBER);
    }

    public static void deleteAgreeAll(BookUser bookUser){
        if(!bookUser.isDeleteAgree()) throw new GlobalException(DELETE_NOT_AGREED);
    }

    private static boolean checkIsNull(BookUser bookUser) {
        return bookUser == null;
    }
}