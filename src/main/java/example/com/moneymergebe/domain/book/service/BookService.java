package example.com.moneymergebe.domain.book.service;

import static example.com.moneymergebe.domain.record.entity.RecordType.EXPENSE;
import static example.com.moneymergebe.domain.record.entity.RecordType.INCOME;

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
import example.com.moneymergebe.domain.book.dto.response.BookMonthAnalysisRes;
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
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
                .color(req.getColor())
                .build()
        );

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

        checkBookMember(user, book); // 가계부 권한 검사

        //가계부 사용자 목록
        List<BookUser> bookUserList = bookUserRepository.findAllByBook(book);
        List<UserGetRes> userGetResList = new ArrayList<>();
        for (BookUser bookUser : bookUserList) {
            userGetResList.add(new UserGetRes(bookUser.getUser().getUserId(), bookUser.getName(), bookUser.getColor()));
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

        return new BookGetRes(book, userGetResList, income, outcome, total);
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
        User newUser = userRepository.findByEmail(req.getEmail());
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
        int totalExpenseSum = recordList.stream()
            .filter(record -> record.getRecordType() == EXPENSE)
            .mapToInt(Record::getAmount)
            .sum(); // 모든 amount 값을 합산

        int totalIncomeSum = recordList.stream()
            .filter(record -> record.getRecordType() == INCOME)
            .mapToInt(Record::getAmount)
            .sum(); // 모든 amount 값을 합산

        // 카테고리별 합산
        Map<Category, Integer> categoryMap = new HashMap<>();
        for(Record record : recordList) {
            if(record.getRecordType() == EXPENSE)
                categoryMap.put(record.getCategory(), categoryMap.getOrDefault(record.getCategory(), 0) + record.getAmount());
        }

        List<CategoryAnalysisRes> categoryResList = new ArrayList<>();
        for(Category category : categoryList) {
            int sum = categoryMap.getOrDefault(category, 0);
            if(sum != 0) categoryResList.add(new CategoryAnalysisRes(category.getCategoryId(), category.getCategory(), sum));
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
        monthResList.add(new MonthAnalysisRes(month, totalIncomeSum, totalExpenseSum));

        return new BookMonthAnalysisRes(totalIncomeSum, totalExpenseSum, categoryResList, monthResList);
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