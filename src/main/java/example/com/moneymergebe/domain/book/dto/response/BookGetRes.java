package example.com.moneymergebe.domain.book.dto.response;

import example.com.moneymergebe.domain.book.entity.Book;
import lombok.Getter;

@Getter
public class BookGetRes {
    private Long bookId;
    private String bookTitle;
    private String bookColor;

    public BookGetRes(Book book) {
        this.bookId = book.getBookId();
        this.bookTitle = book.getTitle();
        this.bookColor = book.getColor();
    }
}
