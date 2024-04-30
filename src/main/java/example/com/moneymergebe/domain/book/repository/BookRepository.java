package example.com.moneymergebe.domain.book.repository;

import example.com.moneymergebe.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

}
