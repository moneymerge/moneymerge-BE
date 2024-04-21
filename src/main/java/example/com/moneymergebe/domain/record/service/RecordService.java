package example.com.moneymergebe.domain.record.service;

import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.book.repository.BookRepository;
import example.com.moneymergebe.domain.book.repository.BookUserRepository;
import example.com.moneymergebe.domain.record.dto.request.RecordSaveReq;
import example.com.moneymergebe.domain.record.dto.response.RecordSaveRes;
import example.com.moneymergebe.domain.record.entity.Record;
import example.com.moneymergebe.domain.record.repository.RecordRepository;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.exception.GlobalException;
import example.com.moneymergebe.global.validator.BookUserValidator;
import example.com.moneymergebe.global.validator.BookValidator;
import example.com.moneymergebe.global.validator.UserValidator;
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
            checkBookMember(user, book);
            Record record = Record.builder().date(req.getDate()).recordType(req.getRecordType()).amount(req.getAmount()).assetType(req.getAssetType())
                .content(req.getContent()).memo(req.getMemo()).image(req.getImage()).book(book).user(user).build(); // TODO: 카테고리 추가
            recordRepository.save(record);
        }
        return new RecordSaveRes();
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
    private void checkBookMember(User user, Book book) {
        BookUser bookUser = bookUserRepository.findByUserAndBook(user, book);
        BookUserValidator.checkMember(bookUser);
    }
}
