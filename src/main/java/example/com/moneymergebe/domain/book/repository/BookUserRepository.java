package example.com.moneymergebe.domain.book.repository;

import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookUserRepository extends JpaRepository<BookUser, Long> {
    BookUser findByUserAndBook(User user, Book book);
}
