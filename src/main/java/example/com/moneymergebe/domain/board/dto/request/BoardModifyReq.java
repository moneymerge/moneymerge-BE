package example.com.moneymergebe.domain.board.dto.request;

import example.com.moneymergebe.domain.board.entity.BoardType;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BoardModifyReq {
    private Long boardId; // 현재 작성하는 게시글 ID
    private Long userId;  // 현재 작성하는 사용자 ID
    private BoardType boardType;
    private String title;
    private String content;
    @Nullable
    private MultipartFile image;
}
