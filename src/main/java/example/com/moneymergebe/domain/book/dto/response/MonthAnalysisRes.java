package example.com.moneymergebe.domain.book.dto.response;

import lombok.Getter;

@Getter
public class MonthAnalysisRes {
    private int month;
    private int income;
    private int expense;

    public MonthAnalysisRes(int month, int income, int expense) {
        this.month = month;
        this.income = income;
        this.expense = expense;
    }
}
