package example.com.moneymergebe.domain.receipt.service;

import example.com.moneymergebe.domain.receipt.dto.request.ReceiptSaveReq;
import example.com.moneymergebe.domain.receipt.dto.response.ReceiptSaveRes;
import example.com.moneymergebe.domain.receipt.entity.Receipt;
import example.com.moneymergebe.domain.receipt.repository.ReceiptLikeRepository;
import example.com.moneymergebe.domain.receipt.repository.ReceiptRepository;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.exception.GlobalException;
import example.com.moneymergebe.global.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final ReceiptLikeRepository receiptLikeRepository;
    private final UserRepository userRepository;

    /**
     * 영수증 생성
     */
    @Transactional
    public ReceiptSaveRes saveReceipt(ReceiptSaveReq req) {
        User user = findUser(req.getUserId());
        Receipt receipt = Receipt.builder().date(req.getDate()).content(req.getContent()).shared(req.isShared())
            .positive(req.getPositive()).negative(req.getNegative()).user(user).build();
        receiptRepository.save(receipt);

        return new ReceiptSaveRes();
    }

    /**
     * @throws GlobalException userId에 해당하는 사용자가 존재하지 않는 경우 예외 발생
     */
    private User findUser(Long userId) {
        User user = userRepository.findByUserId(userId);
        UserValidator.validate(user);
        return user;
    }

}
