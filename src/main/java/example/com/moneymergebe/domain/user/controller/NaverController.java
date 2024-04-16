package example.com.moneymergebe.domain.user.controller;

import static example.com.moneymergebe.global.jwt.JwtUtil.ACCESS_TOKEN_HEADER;
import static example.com.moneymergebe.global.jwt.JwtUtil.REFRESH_TOKEN_HEADER;


import com.fasterxml.jackson.core.JsonProcessingException;
import example.com.moneymergebe.domain.user.service.NaverService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
//@RequestMapping("")
public class NaverController {
    private final NaverService naverService;

    // 네이버 로그인 페이지 가져오기
    @GetMapping("/api/users/naver/page")
    public ResponseEntity<String> getNaverLoginPage() {
        return new ResponseEntity<>(naverService.getNaverLoginPage(), HttpStatus.OK);
    }

    // 네이버 로그인
    @GetMapping("/auth/naver/callback")
    public String naverLogin(@RequestParam String code, HttpServletResponse res)
        throws JsonProcessingException {
        HashMap<String, String> tokens = naverService.naverLogin(code);

        addCookie(tokens.get(ACCESS_TOKEN_HEADER), ACCESS_TOKEN_HEADER, res);
        addCookie(tokens.get(REFRESH_TOKEN_HEADER), REFRESH_TOKEN_HEADER, res);

        System.out.println("네이버 로그인 완료");
        return "login"; // 로그인 완료시 이동할 페이지
    }

    private void addCookie(String cookieValue, String header, HttpServletResponse res) {
        Cookie cookie = new Cookie(header, cookieValue); // Name-Value
        cookie.setPath("/");
        cookie.setMaxAge(2 * 60 * 60);
        res.addCookie(cookie);
    }
}
