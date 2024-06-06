package example.com.moneymergebe.domain.board.dto.request;

import example.com.moneymergebe.domain.board.entity.BoardType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardSaveReq {
    private BoardType boardType;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private Long userId;
}
