package example.com.moneymergebe.domain.user.controller;

import static example.com.moneymergebe.global.jwt.JwtUtil.ACCESS_TOKEN_HEADER;
import static example.com.moneymergebe.global.jwt.JwtUtil.REFRESH_TOKEN_HEADER;

import com.fasterxml.jackson.core.JsonProcessingException;
import example.com.moneymergebe.domain.user.dto.response.LoginRes;
import example.com.moneymergebe.domain.user.service.KakaoService;
import example.com.moneymergebe.global.response.CommonResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
public class KakaoController {
    private final KakaoService kakaoService;

    // 카카오 로그인 페이지 가져오기
    @GetMapping("/api/users/kakao/page")
    public ResponseEntity<String> getKakaoLoginPage() {
        return new ResponseEntity<>(kakaoService.getKakaoLoginPage(), HttpStatus.OK);
    }

    // 카카오 로그인
    @ResponseBody
    @GetMapping("/auth/kakao/callback")
    public CommonResponse<LoginRes> kakaoLogin(@RequestParam String code, HttpServletResponse res)
        throws IOException {
        HashMap<String, String> tokens = kakaoService.kakaoLogin(code);

        return CommonResponse.success(new LoginRes(tokens.get(ACCESS_TOKEN_HEADER), tokens.get(REFRESH_TOKEN_HEADER)));
    }
}
