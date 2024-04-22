package example.com.moneymergebe.domain.record.repository;

import example.com.moneymergebe.domain.record.entity.Record;
import example.com.moneymergebe.domain.record.entity.RecordReaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordReactionRepository extends JpaRepository<RecordReaction, Long> {
    int countByRecordAndReaction(Record record, boolean reaction);

    void deleteAllByRecord(Record record);
}
