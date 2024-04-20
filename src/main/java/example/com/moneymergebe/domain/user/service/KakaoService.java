package example.com.moneymergebe.domain.user.service;

import static example.com.moneymergebe.global.jwt.JwtUtil.ACCESS_TOKEN_HEADER;
import static example.com.moneymergebe.global.jwt.JwtUtil.REFRESH_TOKEN_HEADER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonParser;
import example.com.moneymergebe.domain.user.dto.request.KakaoInsertReq;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.entity.UserRole;
import example.com.moneymergebe.domain.user.entity.UserSocialEnum;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.jwt.JwtUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.client-authentication-method}")
    private String kakaoAuthenticationMethod;

    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String kakaoGrantType;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    private String kakaoAuthorizationUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    @Value("Bearer")
    private String tokenType;


    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    public String getKakaoLoginPage() { 
        //카카오 로그인 요청 주소 반환
        return kakaoAuthorizationUri
            + "?client_id="
            + kakaoClientId
            + "&redirect_uri="
            + kakaoRedirectUri
            + "&response_type=code";
    }

    public HashMap<String, String> kakaoLogin(String code) throws JsonProcessingException {
        // HTML에서 인증 코드(code)를 요청하여 전달받음
        HashMap<String, String> tokens = getKakaoTokens(code); // 인증 코드로 토큰 요청 getKakaoTokens (access, refresh)
        // 받은 access 토큰으로 카카오 사용자 정보를 가져옴
        KakaoInsertReq userResource = getKakaoUserInfo(tokens);
        // 사용자 정보 꺼냄
        String email = userResource.getEmail();
        String username = userResource.getUsername();
        String profileUrl = userResource.getProfileUrl();

        User user = userRepository.findByEmail(email);

        // 이메일이 없다면 새로운 user객체로 만들어줌 (회원가입)
        if (user == null) {
            User newUser =
                User.builder()
                    .username(username)
                    .email(email)
                    .role(UserRole.USER)
                    .social(UserSocialEnum.KAKAO)
                    .profileUrl(profileUrl)
                    .points(0)
                    .characterId(0)
                    .build();
            user = userRepository.save(newUser);
        }
        
        // 반환할 토큰 생성
        HashMap<String, String> returnTokens = new HashMap<>();
        String accessToken = jwtUtil.createAccessToken(String.valueOf(user.getUserId()), String.valueOf(user.getRole()));
        String refreshToken = jwtUtil.createRefreshToken(String.valueOf(user.getUserId()), String.valueOf(user.getRole()));
        returnTokens.put(ACCESS_TOKEN_HEADER, accessToken);
        returnTokens.put(REFRESH_TOKEN_HEADER, refreshToken);

        log.info("카카오 이메일 : "+ user.getEmail());
        log.info("카카오 닉네임 : "+ user.getUsername());
        log.info("카카오 프로필URL : "+ user.getProfileUrl());

        return returnTokens;
    }

    public HashMap<String, String> getKakaoTokens(String code) {
        String accessToken = "";
        String refreshToken = "";

        HashMap<String, String> keyAndValues = new HashMap<>();

        keyAndValues.put("tokenUri", kakaoTokenUri);
        keyAndValues.put("authenticationMethod", kakaoAuthenticationMethod);
        keyAndValues.put("grantType", kakaoGrantType);
        keyAndValues.put("clientId", kakaoClientId);
        keyAndValues.put("clientSecret", kakaoClientSecret);
        keyAndValues.put("redirectUri", kakaoRedirectUri);

        try {
            URL url = new URL(keyAndValues.get("tokenUri"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod(keyAndValues.get("authenticationMethod"));
            conn.setDoOutput(true);

            //서버에 데이터(인증코드) 보내기, 토큰 요청
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=" + keyAndValues.get("grantType"));
            sb.append("&client_id=" + keyAndValues.get("clientId"));
            sb.append("&client_secret=" + keyAndValues.get("clientSecret"));
            sb.append("&redirect_uri=" + keyAndValues.get("redirectUri"));
            sb.append("&code=" + code);

            bw.write(sb.toString()); // 서버로 전송
            bw.flush(); // flush()를 호출하지 않으면 버퍼에 쌓인 데이터가 출력 스트림으로 전송x(출력 스트림이 닫힐 때까지 데이터가 보내지지x)

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); //문자열로 응답 받기
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line; //응답을 한 줄씩 읽어서 result 문자열에 추가
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

    public KakaoInsertReq getKakaoUserInfo(HashMap<String, String> tokens) {
        String userInfoUri = "";
        String authenticationMethod = "";

        userInfoUri = kakaoUserInfoUri;
        authenticationMethod = kakaoAuthenticationMethod;

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

        return KakaoInsertReq.of(element, tokens);
    }
}