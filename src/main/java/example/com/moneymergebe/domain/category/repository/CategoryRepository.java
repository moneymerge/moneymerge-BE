package example.com.moneymergebe.domain.category.repository;

import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.category.entity.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
    List<Category> findByBook(Book book);
    Category findByCategoryId(Long categoryId);
}
