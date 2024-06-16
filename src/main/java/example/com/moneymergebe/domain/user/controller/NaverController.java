package example.com.moneymergebe.domain.user.controller;

import static example.com.moneymergebe.global.jwt.JwtUtil.ACCESS_TOKEN_HEADER;
import static example.com.moneymergebe.global.jwt.JwtUtil.REFRESH_TOKEN_HEADER;


import com.fasterxml.jackson.core.JsonProcessingException;
import example.com.moneymergebe.domain.user.dto.response.LoginRes;
import example.com.moneymergebe.domain.user.service.NaverService;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RequiredArgsConstructor
@Controller
public class NaverController {
    private final NaverService naverService;

    // 네이버 로그인 페이지 가져오기
    @GetMapping("/api/users/naver/page")
    public ResponseEntity<String> getNaverLoginPage() {
        return new ResponseEntity<>(naverService.getNaverLoginPage(), HttpStatus.OK);
    }

    // 네이버 로그인
    @CrossOrigin(origins = "http://43.203.66.36:3000")
    @ResponseBody
    @GetMapping("/auth/naver/callback")
    public CommonResponse<LoginRes> naverLogin(@RequestParam String code)
        throws IOException {
        HashMap<String, String> tokens = naverService.naverLogin(code);

        return CommonResponse.success(new LoginRes(tokens.get(ACCESS_TOKEN_HEADER), tokens.get(REFRESH_TOKEN_HEADER)));
    }
}
