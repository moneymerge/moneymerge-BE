package example.com.moneymergebe.domain.user.dto.response;

import example.com.moneymergebe.domain.book.entity.Book;
import lombok.Getter;

@Getter
public class UserBookResDto {
    private Long bookId;
    private String bookTitle;
    private String bookColor;

    public UserBookResDto(Book book) {
        this.bookId = book.getBookId();
        this.bookTitle = book.getTitle();
        this.bookColor = book.getColor();
    }
}
