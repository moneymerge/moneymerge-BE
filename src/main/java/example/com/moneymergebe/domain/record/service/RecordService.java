package example.com.moneymergebe.domain.record.service;

import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.book.repository.BookRepository;
import example.com.moneymergebe.domain.book.repository.BookUserRepository;
import example.com.moneymergebe.domain.record.dto.request.RecordSaveReq;
import example.com.moneymergebe.domain.record.dto.response.RecordGetMonthRes;
import example.com.moneymergebe.domain.record.dto.response.RecordSaveRes;
import example.com.moneymergebe.domain.record.entity.Record;
import example.com.moneymergebe.domain.record.repository.RecordRepository;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.exception.GlobalException;
import example.com.moneymergebe.global.validator.BookUserValidator;
import example.com.moneymergebe.global.validator.BookValidator;
import example.com.moneymergebe.global.validator.UserValidator;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecordService {
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;

    /**
     * 레코드 생성
     */
    @Transactional
    public RecordSaveRes saveRecord(RecordSaveReq req) {
        User user = findUser(req.getUserId());
        for(Long bookId : req.getBookList()) {
            Book book = findBook(bookId);
            checkBookMember(user, book); // 가계부 권한 검사
            Record record = Record.builder().date(req.getDate()).recordType(req.getRecordType()).amount(req.getAmount()).assetType(req.getAssetType())
                .content(req.getContent()).memo(req.getMemo()).image(req.getImage()).book(book).user(user).build(); // TODO: 카테고리 추가
            recordRepository.save(record);
        }
        return new RecordSaveRes();
    }

    /**
     * 월 레코드 조회
     */
    @Transactional(readOnly = true)
    public List<RecordGetMonthRes> getMonthRecords(Long userId, Long bookId, int year, int month) {
        User user = findUser(userId);
        Book book = findBook(bookId);
        BookUser bookUser = checkBookMember(user, book); // 가계부 권한 검사

        LocalDate startDate = LocalDate.of(year, month, 1); // 시작일
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth()); // 마지막일

        return recordRepository.findAllByBookAndDateBetweenOrderByDate(book, startDate, endDate).stream().map(
            record -> new RecordGetMonthRes(record, bookUser)
        ).toList();
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
     * @throws GlobalException bookId에 해당하는 가계부가 존재하지 않는 경우 예외 발생
     */
    private Book findBook(Long bookId) {
        Book book = bookRepository.findByBookId(bookId);
        BookValidator.validate(book);
        return book;
    }

    /**
     * @throws GlobalException user가 book의 멤버가 아닌 경우 예외 발생
     */
    private BookUser checkBookMember(User user, Book book) {
        BookUser bookUser = bookUserRepository.findByUserAndBook(user, book);
        BookUserValidator.checkMember(bookUser);
        return bookUser;
    }
}
