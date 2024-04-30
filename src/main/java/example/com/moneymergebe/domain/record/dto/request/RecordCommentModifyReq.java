package example.com.moneymergebe.domain.record.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecordCommentModifyReq {
    private Long userId;
    private Long bookId;
    private Long recordId;
    private Long commentId;
    private String content;
}
