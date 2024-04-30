package example.com.moneymergebe.domain.record.repository;

import example.com.moneymergebe.domain.record.entity.Record;
import example.com.moneymergebe.domain.record.entity.RecordReaction;
import example.com.moneymergebe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordReactionRepository extends JpaRepository<RecordReaction, Long> {
    int countByRecordAndReaction(Record record, boolean reaction);

    void deleteAllByRecord(Record record);

    RecordReaction findByRecordAndUserAndReaction(Record record, User user, boolean reaction);
}
