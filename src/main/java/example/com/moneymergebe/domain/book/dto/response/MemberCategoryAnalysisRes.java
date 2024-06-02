package example.com.moneymergebe.domain.book.dto.response;

import java.util.List;
import lombok.Getter;

@Getter
public class MemberCategoryAnalysisRes {
    private Long userId;
    private String username;
    private List<CategoryAnalysisRes> categories;

    public MemberCategoryAnalysisRes(Long userId, String username,
        List<CategoryAnalysisRes> categories) {
        this.userId = userId;
        this.username = username;
        this.categories = categories;
    }
}
