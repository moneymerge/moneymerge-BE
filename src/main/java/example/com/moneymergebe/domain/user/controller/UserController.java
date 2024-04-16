package example.com.moneymergebe.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
@RequiredArgsConstructor
public class UserController {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    private String kakaoAuthorizationUri;

    @GetMapping("/api/users/login-page")
    public String loginPage(Model model) {
        model.addAttribute("kakaoAuthorizationUri", kakaoAuthorizationUri);
        model.addAttribute("kakaoClientId", kakaoClientId);
        model.addAttribute("kakaoRedirectUri", kakaoRedirectUri);
        return "login";
    }
}