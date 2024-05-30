package example.com.moneymergebe.domain.book.dto.response;

import lombok.Getter;

@Getter
public class CategoryAnalysisRes {
    private Long categoryId;
    private String categoryName;
    private int categorySum;

    public CategoryAnalysisRes(Long categoryId, String categoryName, int categorySum) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categorySum = categorySum;
    }
}
