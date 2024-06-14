package example.com.moneymergebe.domain.receipt.service;

import static example.com.moneymergebe.domain.notification.entity.NotificationType.RECEIPT_ARRIVED;
import static example.com.moneymergebe.global.response.ResultCode.ALREADY_WRITTEN_RECEIPT_DATE;

import example.com.moneymergebe.domain.notification.entity.Notification;
import example.com.moneymergebe.domain.notification.repository.NotificationRepository;
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
import example.com.moneymergebe.domain.receipt.entity.Receipt;
import example.com.moneymergebe.domain.receipt.entity.ReceiptLike;
import example.com.moneymergebe.domain.receipt.repository.ReceiptLikeRepository;
import example.com.moneymergebe.domain.receipt.repository.ReceiptRepository;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.exception.GlobalException;
import example.com.moneymergebe.global.validator.ReceiptValidator;
import example.com.moneymergebe.global.validator.UserValidator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final ReceiptLikeRepository receiptLikeRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    /**
     * 영수증 월별 조회
     */
    @Transactional(readOnly = true)
    public ReceiptGetMonthRes getMonthReceipt(Long userId, int year, int month) {
        User user = findUser(userId);
        int totalPositive = 0;
        int totalNegative = 0;

        LocalDate startDate = LocalDate.of(year, month, 1); // 시작일
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth()); // 마지막일

        List<Receipt> receiptList = receiptRepository.findAllByUserAndDateBetweenOrderByDate(user, startDate, endDate);
        List<ReceiptGetRes> resList = new ArrayList<>();
        for(Receipt receipt : receiptList) {
            totalPositive += receipt.getPositive();
            totalNegative += receipt.getNegative();
            int likeCount = receiptLikeRepository.countByReceipt(receipt);
            resList.add(new ReceiptGetRes(receipt, likeCount));
        }

        return new ReceiptGetMonthRes(totalPositive, totalNegative, user.getReceivedReceiptId(), resList);
    }

    /**
     * 영수증 조회
     */
    @Transactional
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

        return new ReceiptLikeRes(receiptLikeRepository.countByReceipt(receipt));
    }

    /**
     * 영수증 생성
     */
    @Transactional
    public ReceiptSaveRes saveReceipt(ReceiptSaveReq req) {
        User user = findUser(req.getUserId());
        Receipt receipt = receiptRepository.findByDateAndUser(req.getDate(), user);
        if(receipt != null) throw new GlobalException(ALREADY_WRITTEN_RECEIPT_DATE);

        receiptRepository.save(Receipt.builder().date(req.getDate()).content(req.getContent()).shared(req.isShared())
            .positive(req.getPositive()).negative(req.getNegative()).user(user).build());

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

        receiptRepository.delete(receipt);

        return new ReceiptDeleteRes();
    }

    /**
     * 랜덤 영수증 목록 (최대 6개)
     */
    @Transactional
    public RandomReceiptRes getRandomReceipts(Long userId) {
        User user = findUser(userId);
        UserValidator.checkTodayDrawStatus(user); // 추첨을 했는지 체크
        List<Receipt> receiptList = receiptRepository.findAllBySharedTrueAndUserNot(user);

        // 무작위로 섞기
        Collections.shuffle(receiptList);

        // 첫 6개의 엔티티 ID를 선택하고 리스트로 저장
        return new RandomReceiptRes(receiptList.stream()
            .limit(6)
            .map(Receipt::getReceiptId).toArray(Long[]::new));
    }

    /**
     * 뽑은 영수증 배정
     */
    @Transactional
    public SaveRandomReceiptRes saveRandomReceipt(Long userId, SaveRandomReceiptReq req) {
        User user = findUser(userId);
        UserValidator.checkTodayDrawStatus(user); // 추첨을 했는지 체크
        user.setTodayDrawStatus(true);

        Receipt receipt = receiptRepository.findByReceiptId(req.getPickReceiptId());
        ReceiptValidator.validate(receipt);

        user.updateReceivedReceiptId(req.getPickReceiptId());
        notificationRepository.save(new Notification(RECEIPT_ARRIVED, "", user));

        return new SaveRandomReceiptRes();
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
