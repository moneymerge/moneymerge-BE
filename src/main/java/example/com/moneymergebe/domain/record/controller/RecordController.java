package example.com.moneymergebe.domain.record.controller;

import example.com.moneymergebe.domain.record.dto.request.RecordModifyReq;
import example.com.moneymergebe.domain.record.dto.request.RecordSaveReq;
import example.com.moneymergebe.domain.record.dto.response.RecordGetMonthRes;
import example.com.moneymergebe.domain.record.dto.response.RecordGetRes;
import example.com.moneymergebe.domain.record.dto.response.RecordModifyRes;
import example.com.moneymergebe.domain.record.dto.response.RecordSaveRes;
import example.com.moneymergebe.domain.record.service.RecordService;
import example.com.moneymergebe.global.response.CommonResponse;
import example.com.moneymergebe.global.security.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books/{bookId}/records")
public class RecordController {
    private final RecordService recordService;

    /**
     * 레코드 저장
     * @param userDetails 사용자 정보
     * @param req 레코드 입력 정보
     * @param bookId 가계부 ID
     */
    @PostMapping
    public CommonResponse<RecordSaveRes> saveRecord(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId, @RequestBody RecordSaveReq req) {
        req.setUserId(userDetails.getUser().getUserId());
        req.setBookId(bookId);
        return CommonResponse.success(recordService.saveRecord(req));
    }

    /**
     * 월 레코드 조회
     * @param userDetails 사용자 정보
     * @param bookId 가계부 ID
     * @param year 조회 연도
     * @param month 조회 월
     * @return 해당 날짜의 레코드 리스트
     */
    @GetMapping("/{year}/{month}")
    public CommonResponse<List<RecordGetMonthRes>> getMonthRecords(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId, @PathVariable int year, @PathVariable int month) {
        return CommonResponse.success(recordService.getMonthRecords(userDetails.getUser().getUserId(), bookId, year, month));
    }

    /**
     * 레코드 상세 조회
     * @param userDetails 사용자 정보
     * @param bookId 가계부 ID
     * @param recordId 조회할 레코드 ID
     * @return 레코드 상세 내용 (댓글, 반응 포함)
     */
    @GetMapping("/{recordId}")
    public CommonResponse<RecordGetRes> getRecord(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long bookId, @PathVariable Long recordId) {
        return CommonResponse.success(recordService.getRecord(userDetails.getUser().getUserId(), bookId, recordId));
    }

    /**
     * 레코드 수정
     * @param userDetails 사용자 정보
     * @param bookId 가계부 ID
     * @param recordId 수정할 레코드 ID
     */
    @PutMapping("/{recordId}")
    public CommonResponse<RecordModifyRes> modifyRecord(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId, @PathVariable Long recordId, @RequestBody RecordModifyReq req) {
        req.setUserId(userDetails.getUser().getUserId());
        req.setBookId(bookId);
        req.setRecordId(recordId);
        return CommonResponse.success(recordService.modifyRecord(req));
    }
}
