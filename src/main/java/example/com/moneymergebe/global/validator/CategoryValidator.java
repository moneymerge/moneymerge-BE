package example.com.moneymergebe.global.validator;

import static example.com.moneymergebe.global.response.ResultCode.NOT_FOUND_CATEGORY;

import example.com.moneymergebe.domain.category.entity.Category;
import example.com.moneymergebe.global.exception.GlobalException;

public class CategoryValidator {
    public static void validate(Category category){
        if(checkIsNull(category)) throw new GlobalException(NOT_FOUND_CATEGORY);
    }

    private static boolean checkIsNull(Category category) {
        return category == null;
    }
}
