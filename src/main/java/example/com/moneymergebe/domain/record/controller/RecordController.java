package example.com.moneymergebe.domain.record.controller;

import example.com.moneymergebe.domain.record.dto.request.RecordCommentModifyReq;
import example.com.moneymergebe.domain.record.dto.request.RecordCommentSaveReq;
import example.com.moneymergebe.domain.record.dto.request.RecordModifyReq;
import example.com.moneymergebe.domain.record.dto.request.RecordSaveReq;
import example.com.moneymergebe.domain.record.dto.response.RecordCommentDeleteRes;
import example.com.moneymergebe.domain.record.dto.response.RecordCommentModifyRes;
import example.com.moneymergebe.domain.record.dto.response.RecordCommentSaveRes;
import example.com.moneymergebe.domain.record.dto.response.RecordDeleteRes;
import example.com.moneymergebe.domain.record.dto.response.RecordDislikeRes;
import example.com.moneymergebe.domain.record.dto.response.RecordGetDislikeRes;
import example.com.moneymergebe.domain.record.dto.response.RecordGetLikeRes;
import example.com.moneymergebe.domain.record.dto.response.RecordGetMonthRes;
import example.com.moneymergebe.domain.record.dto.response.RecordGetRes;
import example.com.moneymergebe.domain.record.dto.response.RecordLikeRes;
import example.com.moneymergebe.domain.record.dto.response.RecordModifyRes;
import example.com.moneymergebe.domain.record.dto.response.RecordSaveRes;
import example.com.moneymergebe.domain.record.service.RecordService;
import example.com.moneymergebe.global.response.CommonResponse;
import example.com.moneymergebe.global.security.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
        @PathVariable Long bookId, RecordSaveReq req, @RequestPart(required = false) MultipartFile multipartFile) {
        req.setUserId(userDetails.getUser().getUserId());
        req.setBookId(bookId);
        return CommonResponse.success(recordService.saveRecord(req, multipartFile));
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
    public CommonResponse<RecordGetRes> getRecord(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId, @PathVariable Long recordId) {
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
        @PathVariable Long bookId, @PathVariable Long recordId, RecordModifyReq req, @RequestPart(required = false) MultipartFile multipartFile) {
        req.setUserId(userDetails.getUser().getUserId());
        req.setBookId(bookId);
        req.setRecordId(recordId);
        return CommonResponse.success(recordService.modifyRecord(req, multipartFile));
    }

    /**
     * 레코드 삭제
     * @param userDetails 사용자 정보
     * @param bookId 가계부 ID
     * @param recordId 삭제할 레코드 ID
     */
    @DeleteMapping("/{recordId}")
    public CommonResponse<RecordDeleteRes> deleteRecord(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId, @PathVariable Long recordId) {
        return CommonResponse.success(recordService.deleteRecord(userDetails.getUser().getUserId(), bookId, recordId));
    }

    /**
     * 레코드 좋아요(토글)
     * @param userDetails 사용자 정보
     * @param bookId 가계부 ID
     * @param recordId 레코드 ID
     */
    @PostMapping("/{recordId}/likes")
    public CommonResponse<RecordLikeRes> likeRecord(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId, @PathVariable Long recordId) {
        return CommonResponse.success(recordService.likeRecord(userDetails.getUser().getUserId(), bookId, recordId));
    }

    /**
     * 레코드 싫어요(토글)
     * @param userDetails 사용자 정보
     * @param bookId 가계부 ID
     * @param recordId 레코드 ID
     */
    @PostMapping("/{recordId}/dislikes")
    public CommonResponse<RecordDislikeRes> dislikeRecord(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId, @PathVariable Long recordId) {
        return CommonResponse.success(recordService.dislikeRecord(userDetails.getUser().getUserId(), bookId, recordId));
    }

    /**
     * 레코드 좋아요 수 조회
     * @param userDetails 사용자 정보
     * @param bookId 가계부 ID
     * @param recordId 레코드 ID
     * @return 레코드 좋아요 개수
     */
    @GetMapping("/{recordId}/likes")
    public CommonResponse<RecordGetLikeRes> getRecordLike(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId, @PathVariable Long recordId) {
        return CommonResponse.success(recordService.getRecordLike(userDetails.getUser().getUserId(), bookId, recordId));
    }

    /**
     * 레코드 싫어요 수 조회
     * @param userDetails 사용자 정보
     * @param bookId 가계부 ID
     * @param recordId 레코드 ID
     * @return 레코드 싫어요 개수
     */
    @GetMapping("/{recordId}/dislikes")
    public CommonResponse<RecordGetDislikeRes> getRecordDislike(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId, @PathVariable Long recordId) {
        return CommonResponse.success(recordService.getRecordDislike(userDetails.getUser().getUserId(), bookId, recordId));
    }

    /**
     * 레코드 댓글 생성
     * @param userDetails 사용자 정보
     * @param bookId 가계부 ID
     * @param recordId 레코드 ID
     * @param req 댓글 내용
     */
    @PostMapping("/{recordId}/comments")
    public CommonResponse<RecordCommentSaveRes> saveRecordComment(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId, @PathVariable Long recordId, @RequestBody RecordCommentSaveReq req) {
        req.setUserId(userDetails.getUser().getUserId());
        req.setBookId(bookId);
        req.setRecordId(recordId);
        return CommonResponse.success(recordService.saveRecordComment(req));
    }

    /**
     * 레코드 댓글 수정
     * @param userDetails 사용자 정보
     * @param bookId 가계부 ID
     * @param recordId 레코드 ID
     * @param commentId 수정할 댓글 ID
     * @param req 수정할 댓글 내용
     */
    @PutMapping("/{recordId}/comments/{commentId}")
    public CommonResponse<RecordCommentModifyRes> modifyRecordComment(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId, @PathVariable Long recordId, @PathVariable Long commentId, @RequestBody RecordCommentModifyReq req) {
        req.setUserId(userDetails.getUser().getUserId());
        req.setBookId(bookId);
        req.setRecordId(recordId);
        req.setCommentId(commentId);
        return CommonResponse.success(recordService.modifyRecordComment(req));
    }

    /**
     * 레코드 댓글 삭제
     * @param userDetails 사용자 정보
     * @param bookId 가계부 ID
     * @param recordId 레코드 ID
     * @param commentId 댓글 ID
     */
    @DeleteMapping("/{recordId}/comments/{commentId}")
    public CommonResponse<RecordCommentDeleteRes> deleteRecordComment(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId, @PathVariable Long recordId, @PathVariable Long commentId) {
        return CommonResponse.success(recordService.deleteRecordComment(userDetails.getUser().getUserId(), bookId, recordId, commentId));
    }
}
