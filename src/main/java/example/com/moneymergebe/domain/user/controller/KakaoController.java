package example.com.moneymergebe.domain.user.controller;

import static example.com.moneymergebe.global.jwt.JwtUtil.ACCESS_TOKEN_HEADER;
import static example.com.moneymergebe.global.jwt.JwtUtil.REFRESH_TOKEN_HEADER;

import com.fasterxml.jackson.core.JsonProcessingException;
import example.com.moneymergebe.domain.user.service.KakaoService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class KakaoController {
    private final KakaoService kakaoService;

    // 카카오 로그인 페이지 가져오기
    @GetMapping("/api/users//kakao/page")
    public ResponseEntity<String> getKakaoLoginPage() {
        return new ResponseEntity<>(kakaoService.getKakaoLoginPage(), HttpStatus.OK);
    }

    // 카카오 로그인
    @GetMapping("/auth/kakao/callback")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse res)
        throws JsonProcessingException {
        HashMap<String, String> tokens = kakaoService.kakaoLogin(code);

        addCookie(tokens.get(ACCESS_TOKEN_HEADER), ACCESS_TOKEN_HEADER, res);
        addCookie(tokens.get(REFRESH_TOKEN_HEADER), REFRESH_TOKEN_HEADER, res);

        log.info("카카오 로그인 완료");
        return "login"; // 로그인 완료시 이동할 페이지
    }

    private void addCookie(String cookieValue, String header, HttpServletResponse res) {
        Cookie cookie = new Cookie(header, cookieValue); // Name-Value
        cookie.setPath("/");
        cookie.setMaxAge(2 * 60 * 60); //쿠키 유효 기간(s) 2시간
        // Response 객체에 Cookie 추가
        res.addCookie(cookie);
    }
}
