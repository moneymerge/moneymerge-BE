package example.com.moneymergebe.domain.board.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardCommentSaveReq {
    private Long userId;
    private Long boardId;
    private String content;
}
