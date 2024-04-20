package example.com.moneymergebe.domain.user.controller;

import example.com.moneymergebe.domain.user.dto.request.UserImageReqDto;
import example.com.moneymergebe.domain.user.dto.request.UserNameReqDto;
import example.com.moneymergebe.domain.user.dto.response.UserAlarmResDto;
import example.com.moneymergebe.domain.user.dto.response.UserBaseInfoResDto;
import example.com.moneymergebe.domain.user.dto.response.UserImageResDto;
import example.com.moneymergebe.domain.user.dto.response.UserNameResDto;
import example.com.moneymergebe.domain.user.dto.response.UserProfileResDto;
import example.com.moneymergebe.domain.user.service.UserService;
import example.com.moneymergebe.global.response.CommonResponse;
import example.com.moneymergebe.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/login-page")
    public String loginPage() {
        return "login";
    }

    /**
     * 기본 정보 조회
     * @param userDetails 사용자 정보
     * @return 사용자 정보, 사용자 가계부 정보
     */
    @ResponseBody
    @GetMapping()
    public CommonResponse<UserBaseInfoResDto> getBaseInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return CommonResponse.success(userService.getBaseInfo(userDetails.getUser().getUserId()));
    }

    /**
     * 프로필 조회
     * @param userDetails 사용자 정보
     * @return 사용자 정보
     */
    @ResponseBody
    @GetMapping("/profile")
    public CommonResponse<UserProfileResDto> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return CommonResponse.success(userService.getProfile(userDetails.getUser().getUserId()));
    }

    /**
     * 닉네임 수정
     * @param userDetails 사용자 정보
     * @param req 사용자 id, 변경할 닉네임
     * @return {}
     */
    @ResponseBody
    @PatchMapping("/username")
    public CommonResponse<UserNameResDto> updateUsername(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody @Valid UserNameReqDto req) {
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(userService.updateUsername(req));
    }

    /**
     * 알람 설정
     * @param userDetails 사용자 정보
     * @return {}
     */
    @ResponseBody
    @PatchMapping("/alarm")
    public CommonResponse<UserAlarmResDto> clickAlarm(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return CommonResponse.success(userService.clickAlarm(userDetails.getUser().getUserId()));
    }

    /**
     * 프로필 이미지 수정
     * @param userDetails 사용자 정보
     * @param multipartFile 변경할 프로필 이미지
     * @return {}
     */
    @ResponseBody
    @PatchMapping("/profile-image")
    public CommonResponse<UserImageResDto> updateProfileImage(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestPart MultipartFile multipartFile) {
        UserImageReqDto req = new UserImageReqDto(userDetails.getUser().getUserId(), multipartFile);
        return CommonResponse.success(userService.updateProfileImage(req));
    }
}
