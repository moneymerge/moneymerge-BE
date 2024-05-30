package example.com.moneymergebe.domain.book.dto.response;

import example.com.moneymergebe.domain.book.dto.response.CategoryAnalysisRes;
import java.util.List;
import lombok.Getter;

@Getter
public class BookMonthAnalysisRes {
    private int incomeSum;
    private int expenseSum;
    private List<CategoryAnalysisRes> categories;
    private List<MonthAnalysisRes> months; // 항상 1월부터

    public BookMonthAnalysisRes(int incomeSum, int expenseSum, List<CategoryAnalysisRes> categories,
        List<MonthAnalysisRes> months) {
        this.incomeSum = incomeSum;
        this.expenseSum = expenseSum;
        this.categories = categories;
        this.months = months;
    }
}
