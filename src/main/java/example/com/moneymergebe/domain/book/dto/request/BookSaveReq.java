package example.com.moneymergebe.domain.book.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSaveReq {
    private Long userId; // 가계부 생성한 사용자 ID
    private String title;
    private String color;
    private Long yearGoal;
    private Long monthGoal;
    private int startDate;
    private Long[] userList; // 가계부 공유하는 사용자들 ID (가계부 생성한 사용자 포함)
}