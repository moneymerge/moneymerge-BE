package example.com.moneymergebe.domain.user.controller;

import example.com.moneymergebe.domain.user.dto.request.UserImageReq;
import example.com.moneymergebe.domain.user.dto.request.UserNameReq;
import example.com.moneymergebe.domain.user.dto.response.UserAlarmRes;
import example.com.moneymergebe.domain.user.dto.response.UserBaseInfoRes;
import example.com.moneymergebe.domain.user.dto.response.UserCharacterRes;
import example.com.moneymergebe.domain.user.dto.response.UserDeleteRes;
import example.com.moneymergebe.domain.user.dto.response.UserImageRes;
import example.com.moneymergebe.domain.user.dto.response.UserInfoRes;
import example.com.moneymergebe.domain.user.dto.response.UserLogoutRes;
import example.com.moneymergebe.domain.user.dto.response.UserNameRes;
import example.com.moneymergebe.domain.user.dto.response.UserPointRes;
import example.com.moneymergebe.domain.user.dto.response.UserProfileRes;
import example.com.moneymergebe.domain.user.service.UserService;
import example.com.moneymergebe.global.jwt.JwtUtil;
import example.com.moneymergebe.global.redis.RedisUtil;
import example.com.moneymergebe.global.response.CommonResponse;
import example.com.moneymergebe.global.security.UserDetailsImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    private static final String LOGOUT_VALUE = "logout";

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
    public CommonResponse<UserBaseInfoRes> getBaseInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return CommonResponse.success(userService.getBaseInfo(userDetails.getUser().getUserId()));
    }

    /**
     * 게시판 프로필 조회
     * @param userId 조회할 사용자의 ID
     * @return 사용자 정보(닉네임, 프로필사진, 캐릭터)
     */
    @ResponseBody
    @GetMapping("/{userId}")
    public CommonResponse<UserInfoRes> getUserInfo(@PathVariable Long userId) {
        return CommonResponse.success(userService.getUserInfo(userId));
    }

    /**
     * 프로필 조회
     * @param userDetails 사용자 정보
     * @return 사용자 정보
     */
    @ResponseBody
    @GetMapping("/profile")
    public CommonResponse<UserProfileRes> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
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
    public CommonResponse<UserNameRes> updateUsername(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody @Valid UserNameReq req) {
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
    public CommonResponse<UserAlarmRes> clickAlarm(@AuthenticationPrincipal UserDetailsImpl userDetails) {
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
    public CommonResponse<UserImageRes> updateProfileImage(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestPart MultipartFile multipartFile) {
        UserImageReq req = new UserImageReq(userDetails.getUser().getUserId(), multipartFile);
        return CommonResponse.success(userService.updateProfileUrl(req));
    }

    /**
     * 사용자 포인트 조회
     * @param userDetails 사용자 정보
     * @return 사용자 보유 포인트
     */
    @ResponseBody
    @GetMapping("/point")
    public CommonResponse<UserPointRes> getUserPoint(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return CommonResponse.success(userService.getUserPoint(userDetails.getUser().getUserId()));
    }

    /**
     * 사용자 캐릭터 조회
     * @param userDetails 사용자 정보
     * @return 사용자 현재 캐릭터
     */
    @ResponseBody
    @GetMapping("/character")
    public CommonResponse<UserCharacterRes> getUserCharacter(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return CommonResponse.success(userService.getUserCharacter(userDetails.getUser().getUserId()));
    }

    /**
     * 로그아웃
     */
    @ResponseBody
    @PostMapping("/logout")
    public CommonResponse<UserLogoutRes> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);
        log.info("refreshToken: {}", refreshToken);

        // 블랙리스트 처리: Redis에 저장
        redisUtil.set(refreshToken, LOGOUT_VALUE, jwtUtil.getExpiration(refreshToken));

        // TODO: 쿠키 삭제(FE)
        deleteCookie(response, JwtUtil.ACCESS_TOKEN_HEADER);
        deleteCookie(response, JwtUtil.REFRESH_TOKEN_HEADER);

        return CommonResponse.success(new UserLogoutRes());
    }

    /**
     * 회원탈퇴
     * @param userDetails 사용자 정보
     */
    @ResponseBody
    @DeleteMapping
    public CommonResponse<UserDeleteRes> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return CommonResponse.success(userService.deleteUser(userDetails.getUser().getUserId()));
    }

    // 쿠키 삭제(임시)
    private void deleteCookie(HttpServletResponse response, String header) {
        Cookie cookie = new Cookie(header, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
