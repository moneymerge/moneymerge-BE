package example.com.moneymergebe.domain.character.controller;

import example.com.moneymergebe.domain.character.dto.request.CharacterBuyReq;
import example.com.moneymergebe.domain.character.dto.request.CharacterChangeReq;
import example.com.moneymergebe.domain.character.dto.response.CharacterBuyRes;
import example.com.moneymergebe.domain.character.dto.response.CharacterChangeRes;
import example.com.moneymergebe.domain.character.dto.response.CharacterGetRes;
import example.com.moneymergebe.domain.character.dto.response.CharacterShopGetRes;
import example.com.moneymergebe.domain.character.dto.response.OwnCharacterRes;
import example.com.moneymergebe.domain.character.service.CharacterService;
import example.com.moneymergebe.global.response.CommonResponse;
import example.com.moneymergebe.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    /**
     * 소유 캐릭터 ID 리스트 조회
     * @param userDetails 사용자 정보
     * @return 사용자 소유 캐릭터 ID 리스트
     */
    @GetMapping("/own")
    public CommonResponse<OwnCharacterRes> getUserOwnCharacters(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return CommonResponse.success(characterService.getUserOwnCharacters(userDetails.getUser().getUserId()));
    }

    /**
     * 캐릭터 변경
     * @param userDetails 사용자 정보
     * @param req 바꿀 캐릭터 ID
     */
    @PatchMapping
    public CommonResponse<CharacterChangeRes> changeCharacter(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CharacterChangeReq req) {
        return CommonResponse.success(characterService.changeCharacter(userDetails.getUser().getUserId(), req));
    }

    /**
     * 상점 캐릭터 조회
     * @param userDetails 사용자 정보
     * @return 상점 캐릭터 내역
     */
    @GetMapping("/shop")
    public CommonResponse<Page<CharacterShopGetRes>> getShoCharacters(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(value = "size", defaultValue = DEFAULT_SIZE) int size,
        @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY) String sortBy,
        @RequestParam(value = "isAsc", defaultValue = DEFAULT_IS_ASC) boolean isAsc
    ) {
        return CommonResponse.success(characterService.getShopCharacters(userDetails.getUser().getUserId(), page - 1, size, sortBy, isAsc));
    }

    /**
     * 상점 캐릭터 구매
     * @param userDetails 사용자 정보
     * @param req 구매할 캐릭터 ID
     */
    @PostMapping
    public CommonResponse<CharacterBuyRes> buyCharacter(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody CharacterBuyReq req) {
        return CommonResponse.success(characterService.buyCharacter(userDetails.getUser().getUserId(), req));
    }
}
