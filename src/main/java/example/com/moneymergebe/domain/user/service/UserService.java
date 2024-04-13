package example.com.moneymergebe.domain.user.service;

import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.user.dto.response.UserBaseInfoResDto;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.exception.GlobalException;
import example.com.moneymergebe.global.response.ResultCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * 기본 정보 조회
     */
    @Transactional(readOnly = true)
    public UserBaseInfoResDto getBaseInfo(Long userId) {
        User user = findUser(userId);
        List<Book> bookList = new ArrayList<>();
        for(BookUser bookUser : user.getBookUserList()) {
            bookList.add(bookUser.getBook());
        }
        return new UserBaseInfoResDto(user, bookList);
    }

    /**
     * @throws GlobalException userId에 해당하는 사용자가 존재하지 않는 경우 예외 발생
     */
    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new GlobalException(ResultCode.NOT_FOUND_USER));
    }

}
