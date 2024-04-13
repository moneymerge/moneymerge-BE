package example.com.moneymergebe.domain.user.service;

import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.user.dto.request.UserNameReqDto;
import example.com.moneymergebe.domain.user.dto.response.UserBaseInfoResDto;
import example.com.moneymergebe.domain.user.dto.response.UserNameResDto;
import example.com.moneymergebe.domain.user.dto.response.UserProfileResDto;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.exception.GlobalException;
import example.com.moneymergebe.global.response.ResultCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
     * 프로필 조회
     */
    @Transactional(readOnly = true)
    public UserProfileResDto getProfile(Long userId) {
        User user = findUser(userId);
        return new UserProfileResDto(user);
    }

    /**
     * 닉네임 수정
     */
    @Transactional
    public UserNameResDto updateUsername(UserNameReqDto req) {
        User user = findUser(req.getUserId());
        verifyUsername(req.getUsername()); // 닉네임 중복 확인
        user.updateUsername(req.getUsername()); // 닉네임 수정
        return new UserNameResDto();
    }

    /**
     * @throws GlobalException userId에 해당하는 사용자가 존재하지 않는 경우 예외 발생
     */
    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new GlobalException(ResultCode.NOT_FOUND_USER));
    }

    /**
     * 닉네임 중복 확인
     * @throws GlobalException username에 해당하는 사용자가 존재하는 경우 예외 발생
     */
    private void verifyUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent()) {
            throw new GlobalException(ResultCode.DUPLICATED_USERNAME);
        }
    }
}
