package example.com.moneymergebe.domain.book.dto.response;

import lombok.Getter;

@Getter
public class CategoryAnalysisRes {
    private Long categoryId;
    private String categoryName;
    private int catExpenseSum;
    private int catIncomeSum;

    public CategoryAnalysisRes(Long categoryId, String categoryName, int catExpenseSum, int catIncomeSum) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.catExpenseSum = catExpenseSum;
        this.catIncomeSum = catIncomeSum;
    }
}
