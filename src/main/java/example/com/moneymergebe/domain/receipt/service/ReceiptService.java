package example.com.moneymergebe.domain.receipt.service;

import example.com.moneymergebe.domain.receipt.dto.request.ReceiptModifyReq;
import example.com.moneymergebe.domain.receipt.dto.request.ReceiptSaveReq;
import example.com.moneymergebe.domain.receipt.dto.response.ReceiptDeleteRes;
import example.com.moneymergebe.domain.receipt.dto.response.ReceiptGetRes;
import example.com.moneymergebe.domain.receipt.dto.response.ReceiptLikeRes;
import example.com.moneymergebe.domain.receipt.dto.response.ReceiptModifyRes;
import example.com.moneymergebe.domain.receipt.dto.response.ReceiptSaveRes;
import example.com.moneymergebe.domain.receipt.entity.Receipt;
import example.com.moneymergebe.domain.receipt.entity.ReceiptLike;
import example.com.moneymergebe.domain.receipt.repository.ReceiptLikeRepository;
import example.com.moneymergebe.domain.receipt.repository.ReceiptRepository;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.exception.GlobalException;
import example.com.moneymergebe.global.validator.ReceiptValidator;
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
     * 영수증 조회
     */
    @Transactional(readOnly = true)
    public ReceiptGetRes getReceipt(Long userId, Long receiptId) {
        User user = findUser(userId);
        Receipt receipt = findReceipt(receiptId);

        ReceiptValidator.checkAuthority(user, receipt); // 권한 검사(나의 영수증이거나 공유받은 영수증인지 검사)
        int likeCount = receiptLikeRepository.countByReceipt(receipt);

        return new ReceiptGetRes(receipt, likeCount);
    }

    /**
     * (공유받은) 영수증 좋아요
     */
    @Transactional
    public ReceiptLikeRes likeReceipt(Long userId, Long receiptId) {
        User user = findUser(userId);
        Receipt receipt = findReceipt(receiptId);

        ReceiptValidator.checkLikeAuthority(user, receipt); // 권한 검사 (공유받은 영수증인지 검사)

        ReceiptLike receiptLike = receiptLikeRepository.findByUserAndReceipt(user, receipt);
        if(receiptLike == null) { // 없으면 생성
            receiptLikeRepository.save(ReceiptLike.builder().user(user).receipt(receipt).build());
        }
        else { // 있으면 삭제
            receiptLikeRepository.delete(receiptLike);
        }

        return new ReceiptLikeRes();
    }

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
     * 영수증 수정
     */
    @Transactional
    public ReceiptModifyRes modifyReceipt(ReceiptModifyReq req) {
        User user = findUser(req.getUserId());
        Receipt receipt = findReceipt(req.getReceiptId());

        ReceiptValidator.checkAuthority(user, receipt.getUser()); // 권한 검사

        receipt.update(req);

        return new ReceiptModifyRes();
    }

    /**
     * 영수증 삭제
     */
    @Transactional
    public ReceiptDeleteRes deleteReceipt(Long userId, Long receiptId) {
        User user = findUser(userId);
        Receipt receipt = findReceipt(receiptId);

        ReceiptValidator.checkAuthority(user, receipt.getUser()); // 권한 검사

        receiptLikeRepository.deleteAllByReceipt(receipt);
        receiptRepository.delete(receipt);

        return new ReceiptDeleteRes();
    }

    /**
     * @throws GlobalException userId에 해당하는 사용자가 존재하지 않는 경우 예외 발생
     */
    private User findUser(Long userId) {
        User user = userRepository.findByUserId(userId);
        UserValidator.validate(user);
        return user;
    }

    /**
     * @throws GlobalException receiptId에 해당하는 영수증이 존재하지 않는 경우 예외 발생
     */
    private Receipt findReceipt(Long receiptId) {
        Receipt receipt = receiptRepository.findByReceiptId(receiptId);
        ReceiptValidator.validate(receipt);
        return receipt;
    }
}
