package example.com.moneymergebe.domain.record.dto.response;

import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.record.entity.Record;
import example.com.moneymergebe.domain.record.entity.RecordType;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class RecordGetMonthRes {
    private Long recordId;
    private String date;
    private String recordType;
    private int amount;
    private Long userId;
    private String userColor;

    public RecordGetMonthRes(Record record, BookUser bookUser) {
        this.recordId = record.getRecordId();
        this.date = record.getDate().toString();
        this.recordType = record.getRecordType().getValue();
        this.amount = record.getAmount();
        this.userId = bookUser.getUser().getUserId();
        this.userColor = bookUser.getColor();
    }
}
