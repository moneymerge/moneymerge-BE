package example.com.moneymergebe.global.scheduler;

import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.book.entity.BookRecord;
import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.book.repository.BookRecordRepository;
import example.com.moneymergebe.domain.book.repository.BookRepository;
import example.com.moneymergebe.domain.book.repository.BookUserRepository;
import example.com.moneymergebe.domain.receipt.entity.Receipt;
import example.com.moneymergebe.domain.receipt.entity.ReceiptLike;
import example.com.moneymergebe.domain.receipt.entity.ReceiptLog;
import example.com.moneymergebe.domain.receipt.repository.ReceiptLikeRepository;
import example.com.moneymergebe.domain.receipt.repository.ReceiptLogRepository;
import example.com.moneymergebe.domain.receipt.repository.ReceiptRepository;
import example.com.moneymergebe.domain.record.dto.response.RecordGetMonthRes;
import example.com.moneymergebe.domain.record.entity.Record;
import example.com.moneymergebe.domain.record.entity.RecordType;
import example.com.moneymergebe.domain.record.repository.RecordRepository;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "Scheduler")
@Component
@RequiredArgsConstructor
public class Scheduler {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;
    private final BookRecordRepository bookRecordRepository;
    private final ReceiptLikeRepository receiptLikeRepository;
    private final ReceiptLogRepository receiptLogRepository;
    private final ReceiptRepository receiptRepository;
    private final Random random = new Random();

    private static final int MONTH_GOAL_POINT = 200;
    private static final int YEAR_GOAL_POINT = 1000;

    // 매일 자정 출석여부를 false로
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetAttendance() {
        List<User> userList = userRepository.findAll();
        for(User user : userList) {
            user.updateAttendance();
        }
    }

    // 매일 자정 영수증 배정
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deliverReceipt() {
        List<User> userList = userRepository.findAll();
        if(userList.isEmpty() || userList.size() == 1) return;
        for(User user : userList) {
            if(user.getReceivedReceiptId() == null) {
                Receipt receipt;
                if(user.getClusterId() != null) receipt = randomReceipt(userRepository.findAllByClusterId(user.getClusterId()), user);
                else receipt = randomReceipt(userList, user);

                if(receipt != null) {
                    user.setReceivedReceiptId(receipt.getReceiptId());
                    receiptLogRepository.save(ReceiptLog.builder().receipt(receipt).user(user).build());
                }
            }
        }
    }

    // 매달 기준일에 월 목표 달성 여부 확인
    @Scheduled(cron = "0 0 0 * * *")
    public void checkMonthGoal() {

        List<Book> bookList = bookRepository.findAll(); // 모든 가계부를 가져옴

        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonth().getValue(); // 현재 월
        int day = LocalDate.now().getDayOfMonth(); // 현재 일

        // 지난 월의 마지막 날을 구함
        YearMonth yearMonth = YearMonth.of(year, month-1);
        int lastDay = yearMonth.lengthOfMonth();

        LocalDate startDate = LocalDate.of(year, month-1, Math.min(day, lastDay)); // 목표 계산 시작일(지난 달 현재 일)

        LocalDate endDate; // 어제 날짜
        if(day == 1) endDate = LocalDate.of(year, month-1, lastDay);
        else endDate = LocalDate.of(year, month, day - 1);

        for(Book book : bookList) {
            // 시작일에 해당하면 월의 총 소비 계산
            if(book.getStartDate() == day) {
                int expensesSum = findExpenseSum(startDate, endDate, book);

                // 월 목표가 같거나 크면 가계부 멤버에게 포인트 지급
                if(expensesSum <= book.getMonthGoal()) {
                    List<BookUser> bookUserList = bookUserRepository.findAllByBook(book);
                    for(BookUser bookUser : bookUserList) {
                        User user = bookUser.getUser();
                        user.updatePoints(user.getPoints() + MONTH_GOAL_POINT);
                    }
                }
            }
        }

    }

    // 12월 31일에 올해 목표 달성 여부 확인
    @Scheduled(cron = "0 0 0 1 1 ?")
    public void checkYearGoal() {
        List<Book> bookList = bookRepository.findAll(); // 모든 가계부를 가져옴

        LocalDate startDate = LocalDate.of(LocalDate.now().getYear()-1, 1, 1); // 시작일
        LocalDate endDate = LocalDate.of(LocalDate.now().getYear()-1, 12, 31); // 마지막일

        // 올해 목표와 올해의 총 소비합 비교
        for(Book book : bookList) {
            int expensesSum = findExpenseSum(startDate, endDate, book);;

            // 올해 목표가 같거나 크면 가계부 멤버에게 포인트 지급
            if(expensesSum <= book.getYearGoal()) {
                List<BookUser> bookUserList = bookUserRepository.findAllByBook(book);
                for(BookUser bookUser : bookUserList) {
                    User user = bookUser.getUser();
                    user.updatePoints(user.getPoints() + YEAR_GOAL_POINT);
                }
            }
        }
    }

    private int findExpenseSum(LocalDate startDate, LocalDate endDate, Book book) {
        int expensesSum = 0;

        List<BookRecord> bookRecordList = bookRecordRepository.findAllByBook(book);
        for (BookRecord bookRecord : bookRecordList) {
            Record record = bookRecord.getRecord();
            if(!record.getDate().isBefore(startDate) && !record.getDate().isAfter(endDate) && record.getRecordType() == RecordType.EXPENSE) {
                expensesSum += record.getAmount();
            }
        }

        return expensesSum;
    }

    private Receipt randomReceipt(List<User> cluster, User user) {
        List<Receipt> availableReceipts;
        int num = 0;
        do {
            User randomUser = randomUser(cluster, user); // 사용자와 같은 그룹에 속하는 임의의 사용자
            List<ReceiptLike> receiptLikeList = receiptLikeRepository.findAllByUser(randomUser); // 임의의 사용자가 좋아요를 누른 영수증 목록
            availableReceipts = receiptLikeList.stream()
                .map(ReceiptLike::getReceipt)
                .filter(receipt -> !receiptLogRepository.existsByUserAndReceipt(user, receipt) && receipt.isShared())
                .toList();
            num++;
            if(num > 5) break;
        } while(availableReceipts.isEmpty()); // 좋아요를 누른 영수증이 없으면 다시 임의의 사용자를 고름

        if(availableReceipts.isEmpty()) {
            List<Receipt> sharedReceipts = receiptRepository.findBySharedTrueAndUserNot(user);
            return sharedReceipts.get(random.nextInt(sharedReceipts.size()));
        }
        return availableReceipts.get(random.nextInt(availableReceipts.size())); // 임의의 영수증
    }

    private User randomUser(List<User> cluster, User user) {
        User randomUser;
        do{
            randomUser = cluster.get(random.nextInt(cluster.size()));
        } while(Objects.equals(randomUser.getUserId(), user.getUserId()));
        return randomUser;
    }
}