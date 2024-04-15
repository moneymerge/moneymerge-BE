package example.com.moneymergebe.domain.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.com.moneymergebe.MockSpringSecurityFilter;
import example.com.moneymergebe.domain.user.dto.request.UserNameReqDto;
import example.com.moneymergebe.domain.user.dto.response.UserAlarmResDto;
import example.com.moneymergebe.domain.user.dto.response.UserBaseInfoResDto;
import example.com.moneymergebe.domain.user.dto.response.UserImageResDto;
import example.com.moneymergebe.domain.user.dto.response.UserNameResDto;
import example.com.moneymergebe.domain.user.dto.response.UserProfileResDto;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.entity.UserRole;
import example.com.moneymergebe.domain.user.service.UserService;
import example.com.moneymergebe.global.security.UserDetailsImpl;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(controllers = {UserController.class})
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    static String TEST_IMAGE_URL = "static/images/sample.jpg";
    static User user;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity(new MockSpringSecurityFilter())).build();

        user = new User("username", "email", "profileUrl", 1L, 200L, true, UserRole.USER);
        UserDetails testUserDetails = new UserDetailsImpl(user);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
    }

    @Test
    @DisplayName("getBaseInfo 테스트")
    void getBaseInfoTest() throws Exception {
        // given
        UserBaseInfoResDto resDto = new UserBaseInfoResDto(user, new ArrayList<>());

        when(userService.getBaseInfo(any())).thenReturn(resDto);

        // when - then
        mockMvc
            .perform(
                get("/api/users").principal(mockPrincipal))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getProfile 테스트")
    void getProfileTest() throws Exception {
        // given
        UserProfileResDto resDto = new UserProfileResDto(user);

        when(userService.getProfile(any())).thenReturn(resDto);

        // when - then
        mockMvc
            .perform(
                get("/api/users/profile").principal(mockPrincipal))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("updateUsername 테스트")
    void updateUsernameTest() throws Exception {
        // given
        UserNameReqDto reqDto = new UserNameReqDto();
        reqDto.setUserId(1L);
        reqDto.setUsername("username");
        UserNameResDto resDto = new UserNameResDto();

        when(userService.updateUsername(any())).thenReturn(resDto);

        // when - then
        mockMvc
            .perform(
                patch("/api/users/username")
                    .content(objectMapper.writeValueAsString(reqDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .principal(mockPrincipal))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("updateUsername 테스트 - 실패: 유효하지 않은 닉네임")
    void updateUsernameFailureTest() throws Exception {
        // given
        UserNameReqDto reqDto = new UserNameReqDto();
        reqDto.setUserId(1L);
        reqDto.setUsername("username1111");
        UserNameResDto resDto = new UserNameResDto();

        when(userService.updateUsername(any())).thenReturn(resDto);

        // when - then
        mockMvc
            .perform(
                patch("/api/users/username")
                    .content(objectMapper.writeValueAsString(reqDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .principal(mockPrincipal))
            .andDo(print())
            .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("clickAlarm 테스트")
    void clickAlarmTest() throws Exception {
        // given
        UserAlarmResDto resDto = new UserAlarmResDto();

        when(userService.clickAlarm(any())).thenReturn(resDto);

        // when - then
        mockMvc
            .perform(
                patch("/api/users/alarm")
                    .principal(mockPrincipal))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("updateProfileImage 테스트")
    void updateProfileImageTest() throws Exception {
        // given
        UserImageResDto resDto = new UserImageResDto();
        Resource resource = new ClassPathResource(TEST_IMAGE_URL);
        MockMultipartFile multipartFile = new MockMultipartFile("multipartFile", resource.getFilename(), IMAGE_JPEG_VALUE, resource.getInputStream());

        when(userService.updateProfileImage(any())).thenReturn(resDto);

        // when - then
        mockMvc
            .perform(
                MockMvcRequestBuilders.multipart(HttpMethod.PATCH, "/api/users/profile-image")
                    .file(multipartFile)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .principal(mockPrincipal))
            .andDo(print())
            .andExpect(status().isOk());
    }
}