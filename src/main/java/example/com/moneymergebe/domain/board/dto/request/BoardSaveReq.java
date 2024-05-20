package example.com.moneymergebe.domain.board.dto.request;

import example.com.moneymergebe.domain.board.entity.BoardType;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BoardSaveReq {
    private BoardType boardType;
    private String title;
    private String content;
    @Nullable
    private MultipartFile image;
    private Long userId;
}
