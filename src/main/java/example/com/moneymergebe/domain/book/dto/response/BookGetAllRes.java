package example.com.moneymergebe.domain.book.dto.response;

import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.user.dto.response.UserGetRes;
import lombok.Getter;

import java.util.List;

@Getter
public class BookGetAllRes {
    private Long bookId;
    private String bookTitle;
    private String bookColor;
    private List<UserGetRes> userList;

    public BookGetAllRes(Book book, List<UserGetRes> userGetResList) {
        this.bookId = book.getBookId();
        this.bookTitle = book.getTitle();
        this.bookColor = book.getColor();
        this.userList = userGetResList;
    }

}
