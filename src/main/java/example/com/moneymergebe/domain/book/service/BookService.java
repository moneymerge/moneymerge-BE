package example.com.moneymergebe.domain.book.service;

import example.com.moneymergebe.domain.book.dto.request.BookColorReq;
import example.com.moneymergebe.domain.book.dto.request.BookMonthGoalReq;
import example.com.moneymergebe.domain.book.dto.request.BookSaveReq;
import example.com.moneymergebe.domain.book.dto.request.BookStartDateReq;
import example.com.moneymergebe.domain.book.dto.request.BookTitleReq;
import example.com.moneymergebe.domain.book.dto.request.BookUserColorReq;
import example.com.moneymergebe.domain.book.dto.request.BookUsernameReq;
import example.com.moneymergebe.domain.book.dto.request.BookUsersReq;
import example.com.moneymergebe.domain.book.dto.request.BookYearGoalReq;
import example.com.moneymergebe.domain.book.dto.response.BookColorRes;
import example.com.moneymergebe.domain.book.dto.response.BookDeleteAgreeRes;
import example.com.moneymergebe.domain.book.dto.response.BookDeleteRes;
import example.com.moneymergebe.domain.book.dto.response.BookGetRes;
import example.com.moneymergebe.domain.book.dto.response.BookMonthGoalRes;
import example.com.moneymergebe.domain.book.dto.response.BookSaveRes;
import example.com.moneymergebe.domain.book.dto.response.BookStartDateRes;
import example.com.moneymergebe.domain.book.dto.response.BookTitleRes;
import example.com.moneymergebe.domain.book.dto.response.BookUserColorRes;
import example.com.moneymergebe.domain.book.dto.response.BookUsernameRes;
import example.com.moneymergebe.domain.book.dto.response.BookUsersRes;
import example.com.moneymergebe.domain.book.dto.response.BookYearGoalRes;
import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.book.repository.BookRecordRepository;
import example.com.moneymergebe.domain.book.repository.BookRepository;
import example.com.moneymergebe.domain.book.repository.BookUserRepository;
import example.com.moneymergebe.domain.record.dto.response.RecordGetMonthRes;
import example.com.moneymergebe.domain.record.entity.RecordType;
import example.com.moneymergebe.domain.record.service.RecordService;
import example.com.moneymergebe.domain.user.dto.response.UserGetRes;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.exception.GlobalException;
import example.com.moneymergebe.global.validator.BookUserValidator;
import example.com.moneymergebe.global.validator.BookValidator;
import example.com.moneymergebe.global.validator.UserValidator;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;
    private final BookRecordRepository bookRecordRepository;
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
            BookUser bookUser = bookUserRepository.save(
                BookUser.builder().book(book).user(user).build());
        }

        return new BookSaveRes();
    }

    /**
     * 가계부 전체 조회
     */
    @Transactional
    public List<BookGetRes> getAllBooks(Long userId) {
        User user = findUser(userId);
        List<BookUser> bookUserList = bookUserRepository.findAllByUser(user);
        List<BookGetRes> bookGetResList = new ArrayList<>();
        for (BookUser bookUser : bookUserList) {
            bookGetResList.add(new BookGetRes(bookUser.getBook()));
        }
        return bookGetResList;
    }

    /**
     * 가계부 상세 조회
     */
    @Transactional
    public BookGetRes getBook(Long userId, Long bookId) {

        User user = findUser(userId);
        Book book = findBook(bookId);

        BookUser bookUser = checkBookMember(user, book); // 가계부 권한 검사

        //가계부 사용자 목록
        List<BookUser> bookUserList = bookUserRepository.findAllByBook(book);
        List<UserGetRes> userGetResList = new ArrayList<>();
        for (BookUser bookUsers : bookUserList) {
            userGetResList.add(new UserGetRes(bookUsers.getUser()));
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
            RecordType recordType = res.getRecordType();
            if(recordType.equals(RecordType.INCOME)){
                income += res.getAmount();
            }
            else if(recordType.equals(RecordType.EXPENSE)){
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
        User user = findUser(req.getUserId());
        checkBookMember(user, book);

        for(Long userId : req.getUserList()){
            User findUser = findUser(userId);
            bookUserRepository.save(BookUser.builder().book(book).user(user).build());
        }

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
        checkBookMember(user, book);

        book.updateUsername(req.getUserId(), req.getUsername());

        return new BookUsernameRes();
    }

    /**
     * 가계부 내 색상 수정
     */
    @Transactional
    public BookUserColorRes updateUserColor(BookUserColorReq req) {
        Book book = findBook(req.getBookId());
        User user = findUser(req.getUserId());
        checkBookMember(user, book);

        book.updateUserColor(req.getUserId(), req.getUserColor());

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

        // bookUser, bookRecord 모두 삭제 후 book 삭제해야함
        bookUserRepository.deleteAllByBook(book);
        bookRecordRepository.deleteAllByBook(book); //RecordService.deleteRecord로?

        bookRepository.delete(book);

        return new BookDeleteRes();
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
}