package example.com.moneymergebe.domain.user.dto.response;

import example.com.moneymergebe.domain.book.dto.response.BookGetRes;
import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class UserBaseInfoResDto {
    private Long userId;
    private String username;
    private String profileUrl;
    private List<BookGetRes> bookList = new ArrayList<>();

    public UserBaseInfoResDto(User user, List<Book> bookList) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.profileUrl = user.getProfileUrl();
        for(Book book : bookList) {
            this.bookList.add(new BookGetRes(book));
        }
    }
}
