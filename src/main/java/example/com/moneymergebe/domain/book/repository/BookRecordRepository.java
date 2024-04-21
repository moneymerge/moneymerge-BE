package example.com.moneymergebe.domain.book.repository;

import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.book.entity.BookRecord;
import example.com.moneymergebe.domain.record.entity.Record;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRecordRepository extends JpaRepository<BookRecord, Long> {
    List<BookRecord> findAllByRecord(Record record);

    List<BookRecord> findAllByBook(Book book);

    BookRecord findByBookAndRecord(Book book, Record record);
}
