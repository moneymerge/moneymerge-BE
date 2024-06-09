package example.com.moneymergebe.domain.record.dto.response;

import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.record.entity.RecordComment;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class RecordCommentGetRes {
    private Long commentId;
    private Long userId;
    private String username;
    private String profileUrl;
    private String content;
    private String createdAt;
    private String modifiedAt;

    public RecordCommentGetRes(RecordComment recordComment, BookUser bookUser) {
        this.commentId = recordComment.getRecordCommentId();
        this.userId = recordComment.getUser().getUserId();
        this.username = bookUser.getName(); // 가계부 내 이름
        this.profileUrl = recordComment.getUser().getProfileUrl();
        this.content = recordComment.getContent();
        this.createdAt = recordComment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.modifiedAt = recordComment.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
