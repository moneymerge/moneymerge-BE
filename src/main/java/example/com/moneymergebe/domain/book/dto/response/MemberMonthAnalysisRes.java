package example.com.moneymergebe.domain.book.dto.response;

import java.util.List;
import lombok.Getter;

@Getter
public class MemberMonthAnalysisRes {
    private Long userId;
    private String username;
    private List<MonthAnalysisRes> months;

    public MemberMonthAnalysisRes(Long userId, String username, List<MonthAnalysisRes> months) {
        this.userId = userId;
        this.username = username;
        this.months = months;
    }
}
