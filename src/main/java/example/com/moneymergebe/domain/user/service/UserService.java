package example.com.moneymergebe.domain.user.service;

import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.character.entity.Character;
import example.com.moneymergebe.domain.character.repository.CharacterRepository;
import example.com.moneymergebe.domain.user.dto.request.UserImageReq;
import example.com.moneymergebe.domain.user.dto.request.UserNameReq;
import example.com.moneymergebe.domain.user.dto.response.UserAlarmRes;
import example.com.moneymergebe.domain.user.dto.response.UserBaseInfoRes;
import example.com.moneymergebe.domain.user.dto.response.UserCharacterRes;
import example.com.moneymergebe.domain.user.dto.response.UserImageRes;
import example.com.moneymergebe.domain.user.dto.response.UserNameRes;
import example.com.moneymergebe.domain.user.dto.response.UserPointRes;
import example.com.moneymergebe.domain.user.dto.response.UserProfileRes;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.exception.GlobalException;
import example.com.moneymergebe.global.response.ResultCode;
import example.com.moneymergebe.global.validator.CharacterValidator;
import example.com.moneymergebe.global.validator.UserValidator;
import example.com.moneymergebe.infra.s3.S3Util;
import example.com.moneymergebe.infra.s3.S3Util.FilePath;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CharacterRepository characterRepository;
    private final S3Util s3Util;

    @Value("${default.image.url}")
    private String defaultProfileImage;

    private static final String IMAGE_JPG = "image/jpeg";
    private static final String IMAGE_PNG = "image/png";

    /**
     * 기본 정보 조회
     */
    @Transactional(readOnly = true)
    public UserBaseInfoRes getBaseInfo(Long userId) {
        User user = findUser(userId);
        List<Book> bookList = new ArrayList<>();
        for(BookUser bookUser : user.getBookUserList()) {
            bookList.add(bookUser.getBook());
        }
        return new UserBaseInfoRes(user, bookList);
    }

    /**
     * 프로필 조회
     */
    @Transactional(readOnly = true)
    public UserProfileRes getProfile(Long userId) {
        User user = findUser(userId);
        return new UserProfileRes(user);
    }

    /**
     * 닉네임 수정
     */
    @Transactional
    public UserNameRes updateUsername(UserNameReq req) {
        User user = findUser(req.getUserId());
        verifyUsername(req.getUsername()); // 닉네임 중복 확인
        user.updateUsername(req.getUsername()); // 닉네임 수정
        return new UserNameRes();
    }

    /**
     * 알람 설정(토글)
     */
    @Transactional
    public UserAlarmRes clickAlarm(Long userId) {
        User user = findUser(userId);
        user.changeAlarm();
        return new UserAlarmRes();
    }

    /**
     * 프로필 사진 수정
     */
    @Transactional
    public UserImageRes updateProfileUrl(UserImageReq req) {
        User user = findUser(req.getUserId());

        String profileUrl = user.getProfileUrl(); // 기존 프로필 이미지
        if (req.getImage() != null && !req.getImage().isEmpty()) { // 새로 입력한 이미지 파일이 있는 경우
            if (!profileUrl.equals(defaultProfileImage) && s3Util.exists(profileUrl, FilePath.PROFILE)) { // 기존 이미지가 기본 프로필이 아니고 존재하는 경우
                s3Util.deleteFile(profileUrl, FilePath.PROFILE); // 기존 이미지 삭제
            }
            checkImage(req.getImage()); // 이미지 파일인지 확인

            profileUrl = s3Util.uploadFile(req.getImage(), FilePath.PROFILE); // 업로드 후 프로필로 설정
        }

        user.updateProfile(profileUrl); // 프로필 업데이트

        return new UserImageRes();
    }

    private void checkImage(MultipartFile multipartFile) {
        String fileType = multipartFile.getContentType();

        if(fileType == null || (!fileType.equals(IMAGE_JPG) && !fileType.equals(IMAGE_PNG))) {
            throw new GlobalException(ResultCode.INVALID_IMAGE_FILE);
        }
    }

    /**
     * 사용자 포인트 조회
     */
    @Transactional(readOnly = true)
    public UserPointRes getUserPoint(Long userId) {
        User user = findUser(userId);
        return new UserPointRes(user.getPoints());
    }

    /**
     * 사용자 캐릭터 조회
     */
    @Transactional(readOnly = true)
    public UserCharacterRes getUserCharacter(Long userId) {
        User user = findUser(userId);
        Character character = findCharacter(user.getCharacterId());
        return new UserCharacterRes(character);
    }

    /**
     * @throws GlobalException userId에 해당하는 사용자가 존재하지 않는 경우 예외 발생
     */
    private User findUser(Long userId) {
        User user = userRepository.findByUserId(userId);
        UserValidator.validate(user);
        return user;
    }

    /**
     * 닉네임 중복 확인
     * @throws GlobalException username에 해당하는 사용자가 존재하는 경우 예외 발생
     */
    private void verifyUsername(String username) {
        UserValidator.checkUsername(userRepository.findByUsername(username));
    }

    /**
     * @throws GlobalException characterId에 해당하는 캐릭터가 존재하지 않는 경우 예외 발생
     */
    private Character findCharacter(Long characterId) {
        Character character = characterRepository.findByCharacterId(characterId);
        CharacterValidator.validate(character);
        return character;
    }
}
