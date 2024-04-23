package example.com.moneymergebe.domain.book.repository;

import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookUserRepository extends JpaRepository<BookUser, Long> {
    List<BookUser> findAllByUser(User user);

    List<BookUser> findAllByBook(Book book);

    BookUser findByUserAndBook(User user, Book book);
    void deleteAllByBook(Book book);
}