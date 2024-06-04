package example.com.moneymergebe.domain.user.dto.response;

import java.util.List;
import lombok.Getter;

@Getter
public class UserSearchListRes {
    private List<UserSearchRes> userSearchList;

    public UserSearchListRes(List<UserSearchRes> userSearchList) {
        this.userSearchList = userSearchList;
    }
}
