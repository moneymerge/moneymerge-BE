package example.com.moneymergebe.domain.category.dto.response;

import example.com.moneymergebe.domain.category.entity.Category;
import lombok.Getter;

@Getter
public class CategoryGetRes {
    private Long categoryId;
    private String category;


    public CategoryGetRes(Category category) {
        this.categoryId = category.getCategoryId();
        this.category = category.getCategory();
    }
}
