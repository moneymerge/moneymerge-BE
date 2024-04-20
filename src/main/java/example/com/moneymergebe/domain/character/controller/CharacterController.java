package example.com.moneymergebe.domain.character.controller;

import example.com.moneymergebe.domain.character.dto.response.CharacterGetRes;
import example.com.moneymergebe.domain.character.service.CharacterService;
import example.com.moneymergebe.global.response.CommonResponse;
import example.com.moneymergebe.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/characters")
public class CharacterController {
    private final CharacterService characterService;

    private static final String DEFAULT_PAGE = "1";
    private static final String DEFAULT_SIZE = "6";
    private static final String DEFAULT_SORT_BY = "createdAt";
    private static final String DEFAULT_IS_ASC = "false";

    /**
     * 소유 캐릭터 조회
     * @param userDetails 사용자 정보
     * @return 사용자 소유 캐릭터 내역
     */
    @GetMapping
    public CommonResponse<Page<CharacterGetRes>> getUserCharacters(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(value = "size", defaultValue = DEFAULT_SIZE) int size,
        @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY) String sortBy,
        @RequestParam(value = "isAsc", defaultValue = DEFAULT_IS_ASC) boolean isAsc
    ) {
        return CommonResponse.success(characterService.getUserCharacters(userDetails.getUser().getUserId(), page - 1, size, sortBy, isAsc));
    }
}
