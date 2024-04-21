package example.com.moneymergebe.domain.record.repository;

import example.com.moneymergebe.domain.record.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {
    Record findByRecordId(Long recordId);
}
