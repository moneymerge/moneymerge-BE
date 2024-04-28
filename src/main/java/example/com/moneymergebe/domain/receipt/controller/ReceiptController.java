package example.com.moneymergebe.domain.receipt.controller;

import example.com.moneymergebe.domain.receipt.dto.request.ReceiptModifyReq;
import example.com.moneymergebe.domain.receipt.dto.request.ReceiptSaveReq;
import example.com.moneymergebe.domain.receipt.dto.response.ReceiptDeleteRes;
import example.com.moneymergebe.domain.receipt.dto.response.ReceiptLikeRes;
import example.com.moneymergebe.domain.receipt.dto.response.ReceiptModifyRes;
import example.com.moneymergebe.domain.receipt.dto.response.ReceiptSaveRes;
import example.com.moneymergebe.domain.receipt.service.ReceiptService;
import example.com.moneymergebe.global.response.CommonResponse;
import example.com.moneymergebe.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
}
