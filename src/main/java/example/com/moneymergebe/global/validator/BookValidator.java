package example.com.moneymergebe.global.validator;

import static example.com.moneymergebe.global.response.ResultCode.NOT_FOUND_BOOK;

import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.global.exception.GlobalException;

public class BookValidator {
    public static void validate(Book book){
        if(checkIsNull(book)) throw new GlobalException(NOT_FOUND_BOOK);
    }

    private static boolean checkIsNull(Book book) {
        return book == null;
    }
}