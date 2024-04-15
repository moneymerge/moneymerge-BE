package example.com.moneymergebe.domain.user.service;

import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.user.dto.request.UserImageReqDto;
import example.com.moneymergebe.domain.user.dto.request.UserNameReqDto;
import example.com.moneymergebe.domain.user.dto.response.UserAlarmResDto;
import example.com.moneymergebe.domain.user.dto.response.UserBaseInfoResDto;
import example.com.moneymergebe.domain.user.dto.response.UserImageResDto;
import example.com.moneymergebe.domain.user.dto.response.UserNameResDto;
import example.com.moneymergebe.domain.user.dto.response.UserProfileResDto;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.exception.GlobalException;
import example.com.moneymergebe.global.response.ResultCode;
import example.com.moneymergebe.infra.s3.S3Util;
import example.com.moneymergebe.infra.s3.S3Util.FilePath;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final S3Util s3Util;

    @Value("${default.image.url}")
    private String defaultProfileImage;

    private static final String IMAGE_JPG = "image/jpeg";
    private static final String IMAGE_PNG = "image/png";

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
     * 알람 설정(토글)
     */
    @Transactional
    public UserAlarmResDto clickAlarm(Long userId) {
        User user = findUser(userId);
        user.changeAlarm();
        return new UserAlarmResDto();
    }

    /**
     * 프로필 사진 수정
     */
    @Transactional
    public UserImageResDto updateProfileImage(UserImageReqDto req) {
        User user = findUser(req.getUserId());

        String profileUrl = user.getProfileUrl(); // 기존 프로필 이미지
        if (req.getImage() != null && !req.getImage().isEmpty()) { // 새로 입력한 이미지 파일이 있는 경우
            if (!profileUrl.equals(defaultProfileImage)) { // 기존 이미지가 기본 프로필이 아닌 경우
                s3Util.deleteFile(profileUrl, FilePath.PROFILE); // 기존 이미지 삭제
            }
            checkImage(req.getImage()); // 이미지 파일인지 확인

            profileUrl = s3Util.uploadFile(req.getImage(), FilePath.PROFILE); // 업로드 후 프로필로 설정
        }

        user.updateProfile(profileUrl); // 프로필 업데이트

        return new UserImageResDto();
    }

    private void checkImage(MultipartFile multipartFile) {
        String fileType = multipartFile.getContentType();

        if(fileType == null || (!fileType.equals(IMAGE_JPG) && !fileType.equals(IMAGE_PNG))) {
            throw new GlobalException(ResultCode.INVALID_IMAGE_FILE);
        }
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
