package example.com.moneymergebe.domain.point.controller;

import example.com.moneymergebe.domain.point.dto.response.PointGetRes;
import example.com.moneymergebe.domain.point.service.PointService;
import example.com.moneymergebe.global.response.CommonResponse;
import example.com.moneymergebe.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
public class PointController {
    private final PointService pointService;

    private static final String DEFAULT_PAGE = "1";
    private static final String DEFAULT_SIZE = "12";
    private static final String DEFAULT_SORT_BY = "createdAt";
    private static final String DEFAULT_IS_ASC = "false";

    /**
     * 포인트 내역 조회
     * @param userDetails 사용자 정보
     * @return 포인트 내역
     */
    @GetMapping
    public CommonResponse<Page<PointGetRes>> getPoints(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(value = "size", defaultValue = DEFAULT_SIZE) int size,
        @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY) String sortBy,
        @RequestParam(value = "isAsc", defaultValue = DEFAULT_IS_ASC) boolean isAsc
        ) {
        return CommonResponse.success(pointService.getPoints(userDetails.getUser().getUserId(), page - 1, size, sortBy, isAsc));
    }
}
