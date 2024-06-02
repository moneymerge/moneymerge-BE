package example.com.moneymergebe.domain.book.dto.response;

import java.util.List;
import lombok.Getter;

@Getter
public class BookMemberAnalysisRes {
    private int incomeSum;
    private int expenseSum;
    private List<MemberIncomeExpenseRes> incomeExpenses;
    private List<MemberCategoryAnalysisRes> categories;
    private List<MemberMonthAnalysisRes> months; // 항상 1월부터

    public BookMemberAnalysisRes(int incomeSum, int expenseSum,
        List<MemberIncomeExpenseRes> incomeExpenses, List<MemberCategoryAnalysisRes> categories,
        List<MemberMonthAnalysisRes> months) {
        this.incomeSum = incomeSum;
        this.expenseSum = expenseSum;
        this.incomeExpenses = incomeExpenses;
        this.categories = categories;
        this.months = months;
    }
}
