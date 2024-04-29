package example.com.moneymergebe.domain.book.dto.response;

import example.com.moneymergebe.domain.book.entity.BookUser;
import lombok.Getter;

@Getter
public class BookDeleteAgreeRes {
    private Boolean deleteAgree;

    public BookDeleteAgreeRes(BookUser bookUser) {
        this.deleteAgree=bookUser.isDeleteAgree();
    }

}
