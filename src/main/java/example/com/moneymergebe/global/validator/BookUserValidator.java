package example.com.moneymergebe.global.validator;

import static example.com.moneymergebe.global.response.ResultCode.NOT_BOOK_MEMBER;

import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.global.exception.GlobalException;

public class BookUserValidator {
    public static void checkMember(BookUser bookUser){
        if(checkIsNull(bookUser)) throw new GlobalException(NOT_BOOK_MEMBER);
    }

    private static boolean checkIsNull(BookUser bookUser) {
        return bookUser == null;
    }
}
