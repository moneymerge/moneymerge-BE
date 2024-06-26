package example.com.moneymergebe.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class UserImageReq {
    private Long userId;
    private MultipartFile image;
}
