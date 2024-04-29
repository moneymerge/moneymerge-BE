package example.com.moneymergebe.domain.category.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryAddReq {
    private Long userId; // 가계부 생성한 사용자 ID
    private String category;
}
