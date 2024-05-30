package example.com.moneymergebe.domain.receipt.controller;

import example.com.moneymergebe.domain.receipt.dto.request.ReceiptModifyReq;
import example.com.moneymergebe.domain.receipt.dto.request.ReceiptSaveReq;
import example.com.moneymergebe.domain.receipt.dto.request.SaveRandomReceiptReq;
import example.com.moneymergebe.domain.receipt.dto.response.RandomReceiptRes;
import example.com.moneymergebe.domain.receipt.dto.response.ReceiptDeleteRes;
import example.com.moneymergebe.domain.receipt.dto.response.ReceiptGetMonthRes;
import example.com.moneymergebe.domain.receipt.dto.response.ReceiptGetRes;
import example.com.moneymergebe.domain.receipt.dto.response.ReceiptLikeRes;
import example.com.moneymergebe.domain.receipt.dto.response.ReceiptModifyRes;
import example.com.moneymergebe.domain.receipt.dto.response.ReceiptSaveRes;
import example.com.moneymergebe.domain.receipt.dto.response.SaveRandomReceiptRes;
import example.com.moneymergebe.domain.receipt.service.ReceiptService;
import example.com.moneymergebe.global.response.CommonResponse;
import example.com.moneymergebe.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/receipts")
public class ReceiptController {
    private final ReceiptService receiptService;

    /**
     * 영수증 월별 조회
     * @param userDetails 사용자 정보
     * @param year 조회하는 연도
     * @param month 조회하는 월
     */
    @GetMapping("/{year}/{month}")
    public CommonResponse<ReceiptGetMonthRes> getMonthReceipt(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable int year, @PathVariable int month) {
        return CommonResponse.success(receiptService.getMonthReceipt(userDetails.getUser().getUserId(), year, month));
    }

    /**
     * 영수증 단일 조회
     * @param userDetails 사용자 정보
     * @param receiptId 조회할 영수증 ID
     */
    @GetMapping("/{receiptId}")
    public CommonResponse<ReceiptGetRes> getReceipt(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long receiptId) {
        return CommonResponse.success(receiptService.getReceipt(userDetails.getUser().getUserId(), receiptId));
    }

    /**
     * (공유받은) 영수증 좋아요
     * @param userDetails 사용자 정보
     * @param receiptId 공유받은 영수증 ID
     */
    @PostMapping("/{receiptId}/likes")
    public CommonResponse<ReceiptLikeRes> likeReceipt(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long receiptId) {
        return CommonResponse.success(receiptService.likeReceipt(userDetails.getUser().getUserId(), receiptId));
    }

    /**
     * 영수증 저장
     * @param userDetails 사용자 정보
     * @param req 영수증 내용
     */
    @PostMapping
    public CommonResponse<ReceiptSaveRes> saveReceipt(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ReceiptSaveReq req) {
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(receiptService.saveReceipt(req));
    }

    /**
     * 영수증 수정
     * @param userDetails 사용자 정보
     * @param receiptId 수정할 영수증 ID
     * @param req 수정할 영수증 정보
     */
    @PutMapping("/{receiptId}")
    public CommonResponse<ReceiptModifyRes> modifyReceipt(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long receiptId, @RequestBody ReceiptModifyReq req) {
        req.setUserId(userDetails.getUser().getUserId());
        req.setReceiptId(receiptId);
        return CommonResponse.success(receiptService.modifyReceipt(req));
    }

    /**
     * 영수증 삭제
     * @param userDetails 사용자 정보
     * @param receiptId 삭제할 영수증 ID
     */
    @DeleteMapping("/{receiptId}")
    public CommonResponse<ReceiptDeleteRes> deleteReceipt(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long receiptId) {
        return CommonResponse.success(receiptService.deleteReceipt(userDetails.getUser().getUserId(), receiptId));
    }

    /**
     * 랜덤 영수증 뽑기
     * @param userDetails 사용자 정보
     * @return 랜덤 영수증 번호 6개
     */
    @GetMapping("/random")
    public CommonResponse<RandomReceiptRes> getRandomReceipts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return CommonResponse.success(receiptService.getRandomReceipts(userDetails.getUser().getUserId()));
    }

    /**
     * 랜덤 영수증 배정
     * @param userDetails 사용자 정보
     * @param req 고른 영수증 번호
     */
    @PatchMapping("/random")
    public CommonResponse<SaveRandomReceiptRes> saveRandomReceipt(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody SaveRandomReceiptReq req) {
        return CommonResponse.success((receiptService.saveRandomReceipt(userDetails.getUser().getUserId(), req)));
    }
}
