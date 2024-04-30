package example.com.moneymergebe.domain.book.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookUserColorReq {
    private Long bookId;
    private Long userId;
    private String userColor;

}
