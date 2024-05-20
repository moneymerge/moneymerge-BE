package example.com.moneymergebe.domain.book.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookUsersReq {
    private Long bookId;
    private Long userId; // 초대 요청 멤버
    private String email; // 초대 받는 멤버
}
