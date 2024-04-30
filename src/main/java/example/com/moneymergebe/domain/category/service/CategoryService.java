package example.com.moneymergebe.domain.category.service;

import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.book.repository.BookRepository;
import example.com.moneymergebe.domain.book.repository.BookUserRepository;
import example.com.moneymergebe.domain.category.dto.request.CategoryAddReq;
import example.com.moneymergebe.domain.category.dto.response.CategoryAddRes;
import example.com.moneymergebe.domain.category.dto.response.CategoryDeleteRes;
import example.com.moneymergebe.domain.category.dto.response.CategoryGetRes;
import example.com.moneymergebe.domain.category.entity.Category;
import example.com.moneymergebe.domain.category.repository.CategoryRepository;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.exception.GlobalException;
import example.com.moneymergebe.global.validator.BookUserValidator;
import example.com.moneymergebe.global.validator.BookValidator;
import example.com.moneymergebe.global.validator.UserValidator;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;

    /**
     * 카테고리 추가
     */
    @Transactional
    public CategoryAddRes addCategory(Long userId, Long bookId, CategoryAddReq req) {
        User user = findUser(userId);
        Book book = findBook(bookId);
        checkBookMember(user, book); // 가계부 권한 검사

        Category category = categoryRepository.save(
            Category.builder()
                .category(req.getCategory())
                .book(book)
                .build()
        );
        return new CategoryAddRes();
    }

    /**
     * 카테고리 조회
     */
    @Transactional
    public List<CategoryGetRes> getCategories(Long bookId){
        Book book = findBook(bookId);
        List<Category> categoryList = categoryRepository.findByBook(book);
        List<CategoryGetRes> categoryGetResList = new ArrayList<>();
        for(Category category : categoryList){
            categoryGetResList.add(new CategoryGetRes(category));
        }
        return categoryGetResList;
    }

    /**
     * 카테고리 삭제
     */
    @Transactional
    public CategoryDeleteRes deleteCategory(Long userId, Long bookId, Long categoryId){
        User user = findUser(userId);
        Book book = findBook(bookId);
        checkBookMember(user, book); // 가계부 권한 검사

        Category category = categoryRepository.findByCategoryId(categoryId);
        categoryRepository.delete(category);

        return new CategoryDeleteRes();
    }


    /**
     * @throws GlobalException userId에 해당하는 사용자가 존재하지 않는 경우 예외 발생
     */
    private User findUser (Long userId){
        User user = userRepository.findByUserId(userId);
        UserValidator.validate(user);
        return user;
    }

    /**
     * @throws GlobalException bookId에 해당하는 가계부가 존재하지 않는 경우 예외 발생
     */
    private Book findBook (Long bookId){
        Book book = bookRepository.findByBookId(bookId);
        BookValidator.validate(book);
        return book;
    }

    /**
     * @throws GlobalException user가 book의 멤버가 아닌 경우 예외 발생
     */
    private BookUser checkBookMember (User user, Book book){
        BookUser bookUser = bookUserRepository.findByUserAndBook(user, book);
        BookUserValidator.checkMember(bookUser);
        return bookUser;
    }
}
