package example.com.moneymergebe.domain.book.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookUsernameReq {
    private Long bookId;
    private Long userId;
    @NotBlank
    private String username;
}
