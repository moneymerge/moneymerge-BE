package example.com.moneymergebe.domain.receipt.controller;

import example.com.moneymergebe.domain.receipt.dto.request.ReceiptSaveReq;
import example.com.moneymergebe.domain.receipt.dto.response.ReceiptSaveRes;
import example.com.moneymergebe.domain.receipt.service.ReceiptService;
import example.com.moneymergebe.global.response.CommonResponse;
import example.com.moneymergebe.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/receipts")
public class ReceiptController {
    private final ReceiptService receiptService;

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
}
