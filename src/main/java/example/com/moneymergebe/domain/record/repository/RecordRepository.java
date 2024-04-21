package example.com.moneymergebe.domain.record.repository;

import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.record.entity.Record;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findAllByBookAndDateBetweenOrderByDate(Book book, LocalDate startDate, LocalDate localDate);

}
