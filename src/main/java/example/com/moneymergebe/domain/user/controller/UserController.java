package example.com.moneymergebe.domain.user.controller;

import example.com.moneymergebe.domain.user.dto.response.UserBaseInfoResDto;
import example.com.moneymergebe.domain.user.service.UserService;
import example.com.moneymergebe.global.jwt.JwtUtil;
import example.com.moneymergebe.global.response.CommonResponse;
import example.com.moneymergebe.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 기본 정보 조회
     * @param userDetails 사용자 정보
     * @return 사용자 정보, 사용자 가계부 정보
     */
    @GetMapping()
    public CommonResponse<UserBaseInfoResDto> getBaseInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return CommonResponse.success(userService.getBaseInfo(userDetails.getUser().getUserId()));
    }

//    토큰 발급하기 위해 임시로 사용
//    @PostMapping("/token")
//    public CommonResponse<String> getAccessToken(HttpServletResponse response) {
//        String accessToken = jwtUtil.createAccessToken("1", "ROLE_USER");
//        addCookie(accessToken, JwtUtil.ACCESS_TOKEN_HEADER, response);
//        return CommonResponse.success(accessToken);
//    }
//
//    private void addCookie(String cookieValue, String header, HttpServletResponse res) {
//        Cookie cookie = new Cookie(header, cookieValue); // Name-Value
//        cookie.setPath("/");
//        cookie.setMaxAge(2 * 60 * 60);
//        //        cookie.setDomain(".sappun.shop");
//        //        cookie.setSecure(true); // 쿠키를 안전한 연결에서만 전송
//        //        cookie.setHttpOnly(true); // JavaScript를 통한 액세스 제한
//
//        // Response 객체에 Cookie 추가
//        res.addCookie(cookie);
//    }

}
