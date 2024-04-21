package example.com.moneymergebe.domain.record.repository;

import example.com.moneymergebe.domain.record.entity.Record;
import example.com.moneymergebe.domain.record.entity.RecordComment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordCommentRepository extends JpaRepository<RecordComment, Long> {
    List<RecordComment> findAllByRecord(Record record);
}
