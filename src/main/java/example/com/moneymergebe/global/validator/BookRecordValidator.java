package example.com.moneymergebe.global.validator;

import static example.com.moneymergebe.global.response.ResultCode.NOT_BOOK_RECORD;

import example.com.moneymergebe.domain.book.entity.BookRecord;
import example.com.moneymergebe.global.exception.GlobalException;

public class BookRecordValidator {
    public static void checkRecord(BookRecord bookRecord){
        if(checkIsNull(bookRecord)) throw new GlobalException(NOT_BOOK_RECORD);
    }

    private static boolean checkIsNull(BookRecord bookRecord) {
        return bookRecord == null;
    }

}
