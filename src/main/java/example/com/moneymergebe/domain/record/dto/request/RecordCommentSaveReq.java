package example.com.moneymergebe.domain.record.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecordCommentSaveReq {
    private Long userId;
    private Long bookId;
    private Long recordId;
    private String content;
}
