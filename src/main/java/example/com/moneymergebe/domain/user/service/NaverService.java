package example.com.moneymergebe.domain.user.service;

import static example.com.moneymergebe.global.jwt.JwtUtil.ACCESS_TOKEN_HEADER;
import static example.com.moneymergebe.global.jwt.JwtUtil.REFRESH_TOKEN_HEADER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonParser;
import example.com.moneymergebe.domain.point.entity.Point;
import example.com.moneymergebe.domain.point.repository.PointRepository;
import example.com.moneymergebe.domain.user.dto.request.NaverInsertReq;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.entity.UserRole;
import example.com.moneymergebe.domain.user.entity.UserSocialEnum;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.jwt.JwtUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverService {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final JwtUtil jwtUtil;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.client-authentication-method}")
    private String naverAuthenticationMethod;

    @Value("${spring.security.oauth2.client.registration.naver.authorization-grant-type}")
    private String naverGrantType;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;

    @Value("${spring.security.oauth2.client.provider.naver.authorization-uri}")
    private String naverAuthorizationUri;

    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String naverTokenUri;

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String naverUserInfoUri;

    @Value("Bearer")
    private String tokenType;

    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String ATTENDANCE = "출석";
    private static final int ATTENDANCE_POINT = 100;

    public String getNaverLoginPage() {
        String state = new BigInteger(130, new SecureRandom()).toString();
        return naverAuthorizationUri
            + "?client_id="
            + naverClientId
            + "&redirect_uri="
            + naverRedirectUri
            + "&response_type=code&state="
            + state;
    }

    public HashMap<String, String> naverLogin(String code) throws JsonProcessingException {
        // HTML에서 인증 코드(code)를 요청하여 전달받음
        HashMap<String, String> tokens = getNaverTokens(code); // 인증 코드로 토큰 요청 getNaverTokens
        // 받은 access 토큰으로 네이버 사용자 정보를 가져옴
        NaverInsertReq userResource = getNaverUserInfo(tokens);

        String email = userResource.getEmail();
        String username = userResource.getUsername();
        String profileUrl = userResource.getProfileUrl();

        // 이메일이 없다면 새로운 user객체로 만들어줌
        User user = userRepository.findByEmail(email);
        if (user == null) {
            User newUser =
                User.builder()
                    .username(username)
                    .email(email)
                    .profileUrl(profileUrl)
                    .role(UserRole.USER)
                    .social(UserSocialEnum.NAVER)
                    .characterId(0)
                    .points(0)
                    .alarm(false)
                    .attendance(false)
                    .build();
            user = userRepository.save(newUser);
        }

        // attendance가 false라면 출석 포인트 적립 후 true로 변경
        if(!user.isAttendance()) {
            pointRepository.save(
                Point.builder().detail(ATTENDANCE).points(ATTENDANCE_POINT).user(user).build());
            user.checkAttendance();
        }

        // 반환할 토큰 생성
        HashMap<String, String> returnTokens = new HashMap<>();
        String accessToken =
            jwtUtil.createAccessToken(String.valueOf(user.getUserId()), String.valueOf(user.getRole()));
        String refreshToken = jwtUtil.createRefreshToken(String.valueOf(user.getUserId()), String.valueOf(user.getRole()));
        returnTokens.put(ACCESS_TOKEN_HEADER, accessToken);
        returnTokens.put(REFRESH_TOKEN_HEADER, refreshToken);

        log.info("네이버 이메일 : "+ user.getEmail());
        log.info("네이버 닉네임 : "+ user.getUsername());
        log.info("네이버 프로필URL : "+ user.getProfileUrl());

        return returnTokens;
    }

    public HashMap<String, String> getNaverTokens(String code) {
        String accessToken = "";
        String refreshToken = "";

        HashMap<String, String> keyAndValues = new HashMap<>();

        keyAndValues.put("tokenUri", naverTokenUri);
        keyAndValues.put("authenticationMethod", naverAuthenticationMethod);
        keyAndValues.put("grantType", naverGrantType);
        keyAndValues.put("clientId", naverClientId);
        keyAndValues.put("clientSecret", naverClientSecret);
        keyAndValues.put("redirectUri", naverRedirectUri);
        keyAndValues.put("state", new BigInteger(130, new SecureRandom()).toString());

        try {
            URL url = new URL(keyAndValues.get("tokenUri"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod(keyAndValues.get("authenticationMethod"));
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=" + keyAndValues.get("grantType"));
            sb.append("&client_id=" + keyAndValues.get("clientId"));
            sb.append("&client_secret=" + keyAndValues.get("clientSecret"));
            sb.append("&redirect_uri=" + keyAndValues.get("redirectUri"));
            sb.append("&code=" + code);
            sb.append("&state=" + keyAndValues.get("state"));
            bw.write(sb.toString());
            bw.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            //확인
            log.info(result);

            accessToken = element.getAsJsonObject().get("access_token").getAsString();
            refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

            br.close();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String, String> tokens = new HashMap<>();
        tokens.put(KEY_ACCESS_TOKEN, accessToken);
        tokens.put(KEY_REFRESH_TOKEN, refreshToken);

        return tokens;
    }

    public NaverInsertReq getNaverUserInfo(HashMap<String, String> tokens) {
        String userInfoUri = "";
        String authenticationMethod = "";

        userInfoUri = naverUserInfoUri;
        authenticationMethod = naverAuthenticationMethod;

        JsonElement element = null;

        try {
            URL url = new URL(userInfoUri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod(authenticationMethod);
            conn.setDoOutput(true);
            conn.setRequestProperty(AUTHORIZATION_HEADER, tokenType + " " + tokens.get(KEY_ACCESS_TOKEN));

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            JsonParser parser = new JsonParser();
            element = parser.parse(result);

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return NaverInsertReq.of(element, tokens);
    }
}
