package example.com.moneymergebe.domain.category.controller;

import example.com.moneymergebe.domain.category.dto.request.CategoryAddReq;
import example.com.moneymergebe.domain.category.dto.response.CategoryAddRes;
import example.com.moneymergebe.domain.category.dto.response.CategoryDeleteRes;
import example.com.moneymergebe.domain.category.dto.response.CategoryGetRes;
import example.com.moneymergebe.domain.category.service.CategoryService;
import example.com.moneymergebe.global.response.CommonResponse;
import example.com.moneymergebe.global.security.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books/{bookId}/categories")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 카테고리 추가
     */
    @PostMapping
    public CommonResponse<CategoryAddRes> addCategory(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId,
        @RequestBody CategoryAddReq req) {
        return CommonResponse.success(categoryService.addCategory(userDetails.getUser().getUserId(), bookId, req));
    }

    /**
     * 카테고리 조회
     */
    @GetMapping
    public CommonResponse<List<CategoryGetRes>> getCategories(@PathVariable Long bookId) {
        return CommonResponse.success(categoryService.getCategories(bookId));
    }

    /**
     * 카테고리 삭제
     */
    @DeleteMapping("/{categoryId}")
    public CommonResponse<CategoryDeleteRes> deleteCategory(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId,
        @PathVariable Long categoryId) {
        return CommonResponse.success(categoryService.deleteCategory(userDetails.getUser().getUserId(), bookId, categoryId));
    }

}
