package example.com.moneymergebe.domain.record.dto.response;

import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.record.entity.Record;
import lombok.Getter;

@Getter
public class RecordGetMonthRes {
    private Long recordId;
    private String date;
    private String recordType;
    private int amount;
    private Long userId;
    private String userColor;
    private Long bookId;

    public RecordGetMonthRes(Record record, BookUser bookUser, Long bookId) {
        this.recordId = record.getRecordId();
        this.date = record.getDate().toString();
        this.recordType = record.getRecordType().getValue();
        this.amount = record.getAmount();
        this.userId = bookUser.getUser().getUserId();
        this.userColor = bookUser.getColor();
        this.bookId = bookId;
    }
}
