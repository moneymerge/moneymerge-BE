package example.com.moneymergebe.domain.book.dto.response;

import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.user.dto.response.UserGetRes;
import java.util.List;
import lombok.Getter;

@Getter
public class BookGetRes {
    private Long bookId;
    private String bookTitle;
    private String bookColor;
    private String myColor;
    private String myName;
    private Long yearGoal;
    private Long monthGoal;
    private int startDate;
    private List<UserGetRes> userList;
    private Long income;
    private Long outcome;
    private Long total;

    public BookGetRes(Book book) {
        this.bookId = book.getBookId();
        this.bookTitle = book.getTitle();
        this.bookColor = book.getColor();
    }

    public BookGetRes(Book book, List<UserGetRes> userGetResList, BookUser bookUser, Long income, Long outcome, Long total) {
        this.bookId = book.getBookId();
        this.bookTitle = book.getTitle();
        this.bookColor = book.getColor();
        this.myColor = bookUser.getColor();
        this.myName = bookUser.getName();
        this.yearGoal = book.getYearGoal();
        this.monthGoal = book.getMonthGoal();
        this.startDate = book.getStartDate();
        this.userList = userGetResList;
        this.income=income;
        this.outcome=outcome;
        this.total=total;
    }
}