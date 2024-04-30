package example.com.moneymergebe.domain.book.repository;

import example.com.moneymergebe.domain.book.entity.BookUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookUserRepository extends JpaRepository<BookUser, Long> {

}
