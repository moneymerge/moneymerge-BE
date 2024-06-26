package example.com.moneymergebe.global.scheduler;

import static example.com.moneymergebe.domain.notification.entity.NotificationType.GOAL_ACHIEVEMENT;
import static example.com.moneymergebe.domain.notification.entity.NotificationType.GOAL_ACHIEVEMENT_FAILURE;

import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.book.entity.BookRecord;
import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.book.repository.BookRecordRepository;
import example.com.moneymergebe.domain.book.repository.BookRepository;
import example.com.moneymergebe.domain.book.repository.BookUserRepository;
import example.com.moneymergebe.domain.notification.entity.Notification;
import example.com.moneymergebe.domain.notification.repository.NotificationRepository;
import example.com.moneymergebe.domain.point.entity.Point;
import example.com.moneymergebe.domain.point.repository.PointRepository;
import example.com.moneymergebe.domain.record.entity.Record;
import example.com.moneymergebe.domain.record.entity.RecordType;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
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
    private final PointRepository pointRepository;
    private final NotificationRepository notificationRepository;

    private static final int MONTH_GOAL_POINT = 1000;
    private static final String MONTH_GOAL = "이번 달 목표 달성";
    private static final int YEAR_GOAL_POINT = 5000;
    private static final String YEAR_GOAL = "올해 목표 달성";

    // 매일 자정 출석여부를 false로 & 공유 받은 영수증 null로
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetAttendance() {
        List<User> userList = userRepository.findAll();
        for(User user : userList) {
            user.updateAttendance();
            user.updateReceivedReceiptId();
            user.setTodayDrawStatus(false);
        }
    }

//    // 매일 자정 영수증 배정
//    @Scheduled(cron = "0 0 0 * * *")
//    @Transactional
//    public void deliverReceipt() {
//        List<User> userList = userRepository.findAll();
//        if(userList.isEmpty() || userList.size() == 1) return;
//        for(User user : userList) {
//            if(user.getReceivedReceiptId() == null) {
//                Receipt receipt;
//                if(user.getClusterId() != null) receipt = randomReceipt(userRepository.findAllByClusterId(user.getClusterId()), user);
//                else receipt = randomReceipt(userList, user);
//
//                if(receipt != null) {
//                    user.setReceivedReceiptId(receipt.getReceiptId());
//                    receiptLogRepository.save(ReceiptLog.builder().receipt(receipt).user(user).build());
//                }
//            }
//        }
//    }

    // 매달 기준일에 월 목표 달성 여부 확인
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
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
                int expenseSum = findExpenseSum(startDate, endDate, book);

                List<BookUser> bookUserList = bookUserRepository.findAllByBook(book);
                for(BookUser bookUser : bookUserList) {
                    User user = bookUser.getUser();
                    // 월 목표가 같거나 크면 가계부 멤버에게 포인트 지급
                    if(expenseSum <= book.getMonthGoal()) {
                        user.updatePoints(MONTH_GOAL_POINT);
                        pointRepository.save(Point.builder().detail(MONTH_GOAL).points(MONTH_GOAL_POINT).user(user).build());
                        notificationRepository.save(new Notification(GOAL_ACHIEVEMENT, Integer.toString(MONTH_GOAL_POINT), user));
                    }
                    else {
                        notificationRepository.save(new Notification(GOAL_ACHIEVEMENT_FAILURE, "월 목표: " + book.getMonthGoal(), user));
                    }
                }
            }
        }

    }

    // 12월 31일에 올해 목표 달성 여부 확인
    @Scheduled(cron = "0 0 0 1 1 ?")
    @Transactional
    public void checkYearGoal() {
        List<Book> bookList = bookRepository.findAll(); // 모든 가계부를 가져옴

        LocalDate startDate = LocalDate.of(LocalDate.now().getYear()-1, 1, 1); // 시작일
        LocalDate endDate = LocalDate.of(LocalDate.now().getYear()-1, 12, 31); // 마지막일

        // 올해 목표와 올해의 총 소비합 비교
        for(Book book : bookList) {
            int expenseSum = findExpenseSum(startDate, endDate, book);;

            List<BookUser> bookUserList = bookUserRepository.findAllByBook(book);
            for(BookUser bookUser : bookUserList) {
                User user = bookUser.getUser();
                // 올해 목표가 같거나 크면 가계부 멤버에게 포인트 지급
                if(expenseSum <= book.getYearGoal()) {
                    user.updatePoints(YEAR_GOAL_POINT);
                    pointRepository.save(Point.builder().detail(YEAR_GOAL).points(YEAR_GOAL_POINT).user(user).build());
                    notificationRepository.save(new Notification(GOAL_ACHIEVEMENT, Integer.toString(YEAR_GOAL_POINT), user));
                }
                else {
                    notificationRepository.save(new Notification(GOAL_ACHIEVEMENT_FAILURE, "연 목표: " + book.getYearGoal(), user));
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

//    private Receipt randomReceipt(List<User> cluster, User user) {
//        List<Receipt> availableReceipts;
//        int num = 0;
//        do {
//            User randomUser = randomUser(cluster, user); // 사용자와 같은 그룹에 속하는 임의의 사용자
//            List<ReceiptLike> receiptLikeList = receiptLikeRepository.findAllByUser(randomUser); // 임의의 사용자가 좋아요를 누른 영수증 목록
//            availableReceipts = receiptLikeList.stream()
//                .map(ReceiptLike::getReceipt)
//                .filter(receipt -> !receiptLogRepository.existsByUserAndReceipt(user, receipt) && receipt.isShared())
//                .toList();
//            num++;
//            if(num > 5) break;
//        } while(availableReceipts.isEmpty()); // 좋아요를 누른 영수증이 없으면 다시 임의의 사용자를 고름
//
//        if(availableReceipts.isEmpty()) {
//            List<Receipt> sharedReceipts = receiptRepository.findBySharedTrueAndUserNot(user);
//            return sharedReceipts.get(random.nextInt(sharedReceipts.size()));
//        }
//        return availableReceipts.get(random.nextInt(availableReceipts.size())); // 임의의 영수증
//    }

//    private User randomUser(List<User> cluster, User user) {
//        User randomUser;
//        do{
//            randomUser = cluster.get(random.nextInt(cluster.size()));
//        } while(Objects.equals(randomUser.getUserId(), user.getUserId()));
//        return randomUser;
//    }
}