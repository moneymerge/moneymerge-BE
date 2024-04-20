package example.com.moneymergebe.domain.user.service;

import static example.com.moneymergebe.global.response.ResultCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import example.com.moneymergebe.domain.user.dto.request.UserImageReqDto;
import example.com.moneymergebe.domain.user.dto.request.UserNameReqDto;
import example.com.moneymergebe.domain.user.dto.response.UserBaseInfoResDto;
import example.com.moneymergebe.domain.user.dto.response.UserProfileResDto;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.entity.UserRole;
import example.com.moneymergebe.domain.user.entity.UserSocialEnum;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.exception.GlobalException;
import example.com.moneymergebe.infra.s3.S3Util;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    S3Util s3Util;

    @InjectMocks
    UserService userService;

    static User user;
    static String TEST_IMAGE_URL = "static/images/sample.jpg";

    @BeforeEach
    void setUp() {
        user = User.builder().username("username").email("email").profileUrl("profileUrl").role(UserRole.USER).social(
            UserSocialEnum.KAKAO).characterId(1).points(200).alarm(true).attendance(true).build();
    }

    @Test
    @DisplayName("getBaseInfo 테스트")
    void getBaseInfoTest() {
        // given
        Long userId = 1L;
        when(userRepository.findByUserId(any())).thenReturn(user);

        // when
        UserBaseInfoResDto resDto = userService.getBaseInfo(userId);

        // then
        assertEquals(user.getUserId(), resDto.getUserId());
        assertEquals(user.getUsername(), resDto.getUsername());
        assertEquals(user.getProfileUrl(), resDto.getProfileUrl());
        assertEquals(0, resDto.getBookList().size());
    }

    @Test
    @DisplayName("getBaseInfo 테스트 - 실패: 사용자를 찾지 못한 경우")
    void getBaseInfoFailureTest() {
        // given
        Long userId = 1L;
        when(userRepository.findByUserId(any())).thenReturn(null);

        // when
        GlobalException exception =
            assertThrows(
                GlobalException.class,
                () -> { userService.getBaseInfo(userId);});

        // then
        assertEquals(NOT_FOUND_USER.getMessage(), exception.getResultCode().getMessage());
    }

    @Test
    @DisplayName("getProfile 테스트")
    void getProfileTest() {
        // given
        Long userId = 1L;
        when(userRepository.findByUserId(any())).thenReturn(user);

        // when
        UserProfileResDto resDto = userService.getProfile(userId);

        // then
        assertEquals(user.getUserId(), resDto.getUserId());
        assertEquals(user.getUsername(), resDto.getUsername());
        assertEquals(user.getProfileUrl(), resDto.getProfileUrl());
        assertEquals(user.getPoints(), resDto.getPoints());
        assertEquals(user.isAlarm(), resDto.isAlarm());
    }

    @Test
    @DisplayName("updateUsername 테스트")
    void updateUsernameTest(){
        // given
        Long userId = 1L;
        String username = "user1";

        UserNameReqDto reqDto = new UserNameReqDto();
        reqDto.setUserId(userId);
        reqDto.setUsername(username);

        when(userRepository.findByUserId(any())).thenReturn(user);
        when(userRepository.findByUsername(any())).thenReturn(null);

        // when
        userService.updateUsername(reqDto);

        // then
        assertEquals(reqDto.getUsername(), user.getUsername());
    }

    @Test
    @DisplayName("updateUsername 테스트 - 실패: 중복된 username")
    void updateUsernameFailureTest(){
        // given
        Long userId = 1L;
        String username = "user1";

        UserNameReqDto reqDto = new UserNameReqDto();
        reqDto.setUserId(userId);
        reqDto.setUsername(username);

        when(userRepository.findByUserId(any())).thenReturn(user);
        when(userRepository.findByUsername(any())).thenReturn(user);

        // when
        GlobalException exception =
            assertThrows(
                GlobalException.class,
                () -> { userService.updateUsername(reqDto);});

        // then
        assertEquals(DUPLICATED_USERNAME.getMessage(), exception.getResultCode().getMessage());
    }

    @Test
    @DisplayName("clickAlarm 테스트")
    void clickAlarmTest() {
        // given
        Long userId = 1L;
        boolean isAlarm = user.isAlarm();
        when(userRepository.findByUserId(any())).thenReturn(user);

        // when
        userService.clickAlarm(userId);

        // then
        assertNotEquals(isAlarm, user.isAlarm());
    }

    @Test
    @DisplayName("updateProfileUrl 테스트")
    void updateProfileUrlTest() throws IOException {
        // given
        Long userId = 1L;
        Resource resource = new ClassPathResource(TEST_IMAGE_URL);
        MultipartFile multipartFile = new MockMultipartFile("image", resource.getFilename(),
            MediaType.IMAGE_JPEG_VALUE, resource.getInputStream());
        UserImageReqDto reqDto = new UserImageReqDto(userId, multipartFile);

        when(userRepository.findByUserId(any())).thenReturn(user);

        String updatedProfileUrl = "updatedProfileUrl";
        when(s3Util.uploadFile(any(), any())).thenReturn(updatedProfileUrl);

        // when
        userService.updateProfileImage(reqDto);

        // then
        assertEquals(updatedProfileUrl, user.getProfileUrl());
    }

    @Test
    @DisplayName("updateProfileUrl 테스트 - 실패: 이미지 파일 타입이 아닌 경우")
    void updateProfileUrlFailureTest() throws IOException {
        // given
        Long userId = 1L;
        Resource resource = new ClassPathResource(TEST_IMAGE_URL);
        MultipartFile multipartFile = new MockMultipartFile("image", resource.getFilename(),
            MediaType.APPLICATION_JSON_VALUE, resource.getInputStream());
        UserImageReqDto reqDto = new UserImageReqDto(userId, multipartFile);

        when(userRepository.findByUserId(any())).thenReturn(user);

        // when
        GlobalException exception =
            assertThrows(
                GlobalException.class,
                () -> { userService.updateProfileImage(reqDto);});

        // then
        assertEquals(INVALID_IMAGE_FILE.getMessage(), exception.getResultCode().getMessage());
    }
}