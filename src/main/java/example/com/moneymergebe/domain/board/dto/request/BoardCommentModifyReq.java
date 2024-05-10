package example.com.moneymergebe.domain.board.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardCommentModifyReq {
    private Long userId;
    private Long boardId;
    private Long commentId;
    private String content;
}
