package example.com.moneymergebe.domain.book.service;

import example.com.moneymergebe.domain.book.dto.request.*;
import example.com.moneymergebe.domain.book.dto.response.*;
import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.book.entity.BookRecord;
import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.book.repository.BookRecordRepository;
import example.com.moneymergebe.domain.book.repository.BookRepository;
import example.com.moneymergebe.domain.book.repository.BookUserRepository;
import example.com.moneymergebe.domain.category.entity.Category;
import example.com.moneymergebe.domain.category.repository.CategoryRepository;
import example.com.moneymergebe.domain.point.entity.Point;
import example.com.moneymergebe.domain.point.repository.PointRepository;
import example.com.moneymergebe.domain.record.dto.response.RecordGetMonthRes;
import example.com.moneymergebe.domain.record.entity.Record;
import example.com.moneymergebe.domain.record.entity.RecordType;
import example.com.moneymergebe.domain.record.service.RecordService;
import example.com.moneymergebe.domain.user.dto.response.UserGetRes;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.exception.GlobalException;
import example.com.moneymergebe.global.validator.BookUserValidator;
import example.com.moneymergebe.global.validator.BookValidator;
import example.com.moneymergebe.global.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static example.com.moneymergebe.domain.record.entity.RecordType.EXPENSE;
import static example.com.moneymergebe.domain.record.entity.RecordType.INCOME;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;
    private final BookRecordRepository bookRecordRepository;
    private final CategoryRepository categoryRepository;
    private final RecordService recordService;
    private final PointRepository pointRepository;

    private final static int MONTH_GOAL_CHANGE_POINT = -500;
    private final static int YEAR_GOAL_CHANGE_POINT = -1000;
    private final static String MONTH_GOAL_CHANGE = "월 목표 수정";
    private final static String YEAR_GOAL_CHANGE = "연 목표 수정";

    /**
     * 가계부 생성
     */
    @Transactional
    public BookSaveRes saveBook(BookSaveReq req) {

        Book book = bookRepository.save(
            Book.builder()
                .startDate(req.getStartDate())
                .title(req.getTitle())
                .yearGoal(req.getYearGoal())
                .monthGoal(req.getMonthGoal())
                .color(req.getBookColor())
                .build()
        );

        BookUser bookUser = bookUserRepository.save(BookUser.builder().book(book).user(findUser(req.getUserId())).build());
        bookUser.updateUserColor(req.getUserColor());
        for (Long userId : req.getUserList()) {
            User user = findUser(userId);
            bookUserRepository.save(BookUser.builder().book(book).user(user).build());
        }

        return new BookSaveRes();
    }

    /**
     * 가계부 전체 조회
     */
    @Transactional(readOnly = true)
    public List<BookGetAllRes> getAllBooks(Long userId) {
        User user = findUser(userId);
        List<BookUser> bookUserList = bookUserRepository.findAllByUser(user);
        List<UserGetRes> userGetResList = new ArrayList<>();
        List<BookGetAllRes> bookGetAllResList = new ArrayList<>();

        for (BookUser bookUser : bookUserList) {
            userGetResList.add(new UserGetRes(bookUser.getUser().getUserId(), bookUser.getName(), bookUser.getColor()));
            bookGetAllResList.add(new BookGetAllRes(bookUser.getBook(), userGetResList));
        }

        return bookGetAllResList;
    }

    /**
     * 가계부 상세 조회
     */
    @Transactional(readOnly = true)
    public BookGetRes getBook(Long userId, Long bookId) {

        User user = findUser(userId);
        Book book = findBook(bookId);

        BookUser bookUserInAccess = checkBookMember(user, book); // 가계부 권한 검사

        // 삭제 동의
        int deleteAgreeNum = 0;

        //가계부 사용자 목록
        List<BookUser> bookUserList = bookUserRepository.findAllByBook(book);
        List<UserGetRes> userGetResList = new ArrayList<>();
        for (BookUser bookUser : bookUserList) {
            userGetResList.add(new UserGetRes(bookUser.getUser().getUserId(), bookUser.getName(), bookUser.getColor()));
            if(bookUser.isDeleteAgree()) {
                deleteAgreeNum++;
            }
        }

        //income
        //월 레코드 가져오기
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        List<RecordGetMonthRes> resList = recordService.getMonthRecords(user.getUserId(), book.getBookId(), year, month);


        //income, outcome, total
        Long income = 0L;
        Long outcome = 0L;
        Long total;
        for(RecordGetMonthRes res : resList){
            String recordType = res.getRecordType();
            if(recordType.equals(RecordType.INCOME.getValue())){
                income += res.getAmount();
            }
            else if(recordType.equals(EXPENSE.getValue())){
                outcome += res.getAmount();
            }
        }
        total = income - outcome;

        return new BookGetRes(book, userGetResList, bookUserInAccess, income, outcome, total, deleteAgreeNum);
    }


    /**
     * 가계부 시작일 수정
     */
    @Transactional
    public BookStartDateRes updateStartDate(BookStartDateReq req) {
        Book book = findBook(req.getBookId());
        User user = findUser(req.getUserId());
        checkBookMember(user, book);

        book.updateStartDate(req.getStartDate());

        return new BookStartDateRes();
    }

    /**
     * 가계부 멤버 초대
     */
    @Transactional
    public BookUsersRes updateUsers(BookUsersReq req){
        Book book = findBook(req.getBookId());
        User user = findUser(req.getUserId()); // 초대 요청 멤버
        checkBookMember(user, book);

        // 초대 받는 멤버가 기존 멤버인지 확인
        User newUser = findUser(req.getInvitedUserId());
        newBookMember(newUser, book);

        bookUserRepository.save(BookUser.builder().book(book).user(newUser).build());

        return new BookUsersRes();
    }


    /**
     * 가계부 제목 수정
     */
    @Transactional
    public BookTitleRes updateBookTitle(BookTitleReq req) {
        Book book = findBook(req.getBookId());
        User user = findUser(req.getUserId());
        checkBookMember(user, book);

        book.updateBookTitle(req.getBookTitle());

        return new BookTitleRes();
    }

    /**
     * 가계부 색상 수정
     */
    @Transactional
    public BookColorRes updateBookColor(BookColorReq req) {
        Book book = findBook(req.getBookId());
        User user = findUser(req.getUserId());
        checkBookMember(user, book);

        book.updateBookColor(req.getBookColor());

        return new BookColorRes();
    }

    /**
     * 가계부 내 이름 수정
     */
    @Transactional
    public BookUsernameRes updateUsername(BookUsernameReq req) {
        Book book = findBook(req.getBookId());
        User user = findUser(req.getUserId());
        BookUser bookUser = checkBookMember(user, book);

        bookUser.updateUsername(req.getUsername());

        return new BookUsernameRes();
    }

    /**
     * 가계부 내 색상 수정
     */
    @Transactional
    public BookUserColorRes updateUserColor(BookUserColorReq req) {
        Book book = findBook(req.getBookId());
        User user = findUser(req.getUserId());
        BookUser bookUser = checkBookMember(user, book);

        bookUser.updateUserColor(req.getUserColor());

        return new BookUserColorRes();
    }

    /**
     * 가계부 올해 목표 수정
     */
    @Transactional
    public BookYearGoalRes updateYearGoal(BookYearGoalReq req) {
        Book book = findBook(req.getBookId());
        User user = findUser(req.getUserId());
        checkBookMember(user, book);

        book.updateYearGoal(req.getYearGoal());
        user.updatePoints(YEAR_GOAL_CHANGE_POINT);
        pointRepository.save(Point.builder().detail(YEAR_GOAL_CHANGE).points(YEAR_GOAL_CHANGE_POINT).user(user).build());

        return new BookYearGoalRes();
    }

    /**
     * 가계부 이번달 목표 수정
     */
    @Transactional
    public BookMonthGoalRes updateMonthGoal(BookMonthGoalReq req) {
        Book book = findBook(req.getBookId());
        User user = findUser(req.getUserId());
        checkBookMember(user, book);

        book.updateMonthGoal(req.getMonthGoal());
        user.updatePoints(MONTH_GOAL_CHANGE_POINT);
        pointRepository.save(Point.builder().detail(MONTH_GOAL_CHANGE).points(MONTH_GOAL_CHANGE_POINT).user(user).build());

        return new BookMonthGoalRes();
    }


    /**
     * 가계부 삭제 동의
     */
    @Transactional
    public BookDeleteAgreeRes deleteAgree(Long userId, Long bookId){
        User user = findUser(userId);
        Book book = findBook(bookId);
        BookUser bookUser = checkBookMember(user, book);

        bookUser.updateDeleteAgree();

        return new BookDeleteAgreeRes(bookUser);
    }

    /**
     * 가계부 삭제
     */
    @Transactional
    public BookDeleteRes deleteBook(Long userId, Long bookId) {
        User user = findUser(userId);
        Book book = findBook(bookId);

        checkBookMember(user, book);

        // 가계부 멤버가 전원 삭제 동의했는지 확인
        deleteAgreeAll(bookId);

        bookRepository.delete(book);

        return new BookDeleteRes();
    }

    /**
     * 가계부 월별 조회
     */
    @Transactional(readOnly = true)
    public BookMonthAnalysisRes analyzeBook(Long userId, Long bookId, int year, int month) {
        User user = findUser(userId);
        Book book = findBook(bookId);

        checkBookMember(user, book);

        List<Category> categoryList = categoryRepository.findAllByBook(book);

        List<Record> recordList = findRecordList(year, month, book);

        // 전체 합산
        int totalIncomeSum = getAmountSum(recordList, INCOME);
        int totalExpenseSum = getAmountSum(recordList, EXPENSE);

        // 카테고리별 합산 (지출)
        Map<Category, Integer> categoryExpenseMap = new HashMap<>();
        for(Record record : recordList) {
            if(record.getRecordType() == EXPENSE)
                categoryExpenseMap.put(record.getCategory(), categoryExpenseMap.getOrDefault(record.getCategory(), 0) + record.getAmount());
        }
        // 카테고리별 합산(수입)
        Map<Category, Integer> categoryIncomeMap = new HashMap<>();
        for(Record record : recordList) {
            if(record.getRecordType() == INCOME)
                categoryIncomeMap.put(record.getCategory(), categoryIncomeMap.getOrDefault(record.getCategory(), 0) + record.getAmount());
        }

        List<CategoryAnalysisRes> categoryResList = new ArrayList<>();
        for(Category category : categoryList) {
            int catExpenseSum = categoryExpenseMap.getOrDefault(category, 0);
            int catIncomeSum = categoryIncomeMap.getOrDefault(category, 0);
//            if(catExpenseSum != 0 && catIncomeSum!=0)
                categoryResList.add(new CategoryAnalysisRes(category.getCategoryId(), category.getCategory(), catExpenseSum, catIncomeSum));
        }

        // 1월부터 월별 분석
        List<MonthAnalysisRes> monthResList = new ArrayList<>();
        for (int i = 1; i < month; i++) {
            List<Record> recordListInMonth = findRecordList(year, i, book);
            int incomeSum = 0;
            int expenseSum = 0;
            for (Record record : recordListInMonth) {
                switch(record.getRecordType()) {
                    case EXPENSE -> expenseSum += record.getAmount();
                    case INCOME -> incomeSum += record.getAmount();
                }
            }
            monthResList.add(new MonthAnalysisRes(i, incomeSum, expenseSum));
        }
        // 현재 월
        monthResList.add(new MonthAnalysisRes(month, totalIncomeSum, totalExpenseSum));

        return new BookMonthAnalysisRes(totalIncomeSum, totalExpenseSum, categoryResList, monthResList);
    }

    /**
     * 가계부 멤버별 조회
     */
    @Transactional(readOnly = true)
    public BookMemberAnalysisRes analyzeMember(Long userId, Long bookId, int year, int month) {
        User user = findUser(userId);
        Book book = findBook(bookId);

        checkBookMember(user, book);

        List<Category> categoryList = categoryRepository.findAllByBook(book);
        List<BookUser> bookUserList = bookUserRepository.findAllByBook(book);

        List<Record> recordList = findRecordList(year, month, book); // 가계부 전체 레코드

        // 전체 합산
        int totalIncomeSum = getAmountSum(recordList, INCOME);
        int totalExpenseSum = getAmountSum(recordList, EXPENSE);

        // 멤버별
        List<MemberIncomeExpenseRes> incomeExpenseRes = new ArrayList<>();
        List<MemberCategoryAnalysisRes> categoryRes = new ArrayList<>();
        List<MemberMonthAnalysisRes> monthRes = new ArrayList<>();

        for(BookUser bookUser : bookUserList) {
            // 수입 지출
            int incomeSum = getAmountSumByUser(recordList, INCOME, bookUser.getUser());
            int expenseSum = getAmountSumByUser(recordList, EXPENSE, bookUser.getUser());

            incomeExpenseRes.add(new MemberIncomeExpenseRes(bookUser.getUser().getUserId(), bookUser.getName(), incomeSum, expenseSum));

            // 카테고리
            Map<Category, Integer> categoryExpenseMap = new HashMap<>();
            for(Record record : recordList) {
                if(record.getRecordType() == EXPENSE && Objects.equals(record.getUser().getUserId(),
                    bookUser.getUser().getUserId()))
                    categoryExpenseMap.put(record.getCategory(), categoryExpenseMap.getOrDefault(record.getCategory(), 0) + record.getAmount());
            }
            Map<Category, Integer> categoryIncomeMap = new HashMap<>();
            for(Record record : recordList) {
                if(record.getRecordType() == INCOME && Objects.equals(record.getUser().getUserId(),
                        bookUser.getUser().getUserId()))
                    categoryIncomeMap.put(record.getCategory(), categoryIncomeMap.getOrDefault(record.getCategory(), 0) + record.getAmount());
            }

            List<CategoryAnalysisRes> categoryResList = new ArrayList<>();
            for(Category category : categoryList) {
                int catExpenseSum = categoryExpenseMap.getOrDefault(category, 0);
                int catIncomeSum = categoryIncomeMap.getOrDefault(category, 0);
//                if(catExpenseSum != 0 && catIncomeSum!=0)
                    categoryResList.add(new CategoryAnalysisRes(category.getCategoryId(), category.getCategory(), catExpenseSum, catIncomeSum));
            }

            categoryRes.add(new MemberCategoryAnalysisRes(bookUser.getUser().getUserId(), bookUser.getName(), categoryResList));
        }

        // 월별
        for(BookUser bookUser : bookUserList) {
            List<MonthAnalysisRes> monthResList = new ArrayList<>();
            for (int i = 1; i <= month; i++) {
                List<Record> recordListInMonth = findRecordList(year, i, book);
                int monthIncomeSum = 0;
                int monthExpenseSum = 0;
                for (Record record : recordListInMonth) {
                    if(Objects.equals(record.getUser().getUserId(), bookUser.getUser().getUserId())) {
                        switch(record.getRecordType()) {
                            case INCOME -> monthIncomeSum += record.getAmount();
                            case EXPENSE -> monthExpenseSum += record.getAmount();
                        }
                    }
                }
                monthResList.add(new MonthAnalysisRes(i, monthIncomeSum, monthExpenseSum));
            }
            monthRes.add(new MemberMonthAnalysisRes(bookUser.getUser().getUserId(), bookUser.getName(), monthResList));
        }

        return new BookMemberAnalysisRes(totalIncomeSum, totalExpenseSum, incomeExpenseRes, categoryRes, monthRes);
    }

    private int getAmountSum(List<Record> recordList, RecordType type) {
        return recordList.stream()
            .filter(record -> record.getRecordType() == type)
            .mapToInt(Record::getAmount)
            .sum(); // 모든 amount 값을 합산
    }

    private int getAmountSumByUser(List<Record> recordList, RecordType type, User user) {
        return recordList.stream()
            .filter(record -> record.getRecordType() == type && Objects.equals(
                record.getUser().getUserId(), user.getUserId()))
            .mapToInt(Record::getAmount)
            .sum();
    }

    private List<Record> findRecordList(int year, int month, Book book) {
        LocalDate startDate = LocalDate.of(year, month, 1); // 목표 계산 시작일
        LocalDate endDate = LocalDate.of(year, month, startDate.lengthOfMonth());

        List<BookRecord> bookRecordList = bookRecordRepository.findAllByBook(book);
        List<Record> list = new ArrayList<>();
        for (BookRecord bookRecord : bookRecordList) {
            Record record = bookRecord.getRecord();
            if(!record.getDate().isBefore(startDate) && !record.getDate().isAfter(endDate)) {
                list.add(record);
            }
        }

        return list;
    }

    /**
     * @throws GlobalException userId에 해당하는 사용자가 존재하지 않는 경우 예외 발생
     */
    private User findUser (Long userId){
        User user = userRepository.findByUserId(userId);
        UserValidator.validate(user);
        return user;
    }

    /**
     * @throws GlobalException bookId에 해당하는 가계부가 존재하지 않는 경우 예외 발생
     */
    private Book findBook (Long bookId){
        Book book = bookRepository.findByBookId(bookId);
        BookValidator.validate(book);
        return book;
    }

    /**
     * @throws GlobalException user가 book의 멤버가 아닌 경우 예외 발생
     */
    private BookUser checkBookMember (User user, Book book){
        BookUser bookUser = bookUserRepository.findByUserAndBook(user, book);
        BookUserValidator.checkMember(bookUser);
        return bookUser;
    }

    /**
     * @throws GlobalException 초대 받는 user가 book의 기존 멤버일 경우 예외 발생
     */
    private void newBookMember (User user, Book book){
        BookUser bookUser = bookUserRepository.findByUserAndBook(user, book);
        BookUserValidator.newMember(bookUser);
    }

    /**
     * @throws GlobalException 가계부 멤버 전원의 삭제 동의를 얻지 못한 경우 예외 발생
     */
    private void deleteAgreeAll (Long bookId){
        Book book = bookRepository.findByBookId(bookId);
        List<BookUser> bookUserList = bookUserRepository.findAllByBook(book);
        for (BookUser bookUser : bookUserList) {
            BookUserValidator.deleteAgreeAll(bookUser);
        }
    }
}