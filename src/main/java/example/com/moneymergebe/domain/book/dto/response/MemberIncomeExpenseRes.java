package example.com.moneymergebe.domain.book.dto.response;

import lombok.Getter;

@Getter
public class MemberIncomeExpenseRes {
    private Long userId;
    private String username;
    private int income;
    private int expense;

    public MemberIncomeExpenseRes(Long userId, String username, int income, int expense) {
        this.userId = userId;
        this.username = username;
        this.income = income;
        this.expense = expense;
    }
}
