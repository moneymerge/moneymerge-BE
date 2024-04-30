package example.com.moneymergebe.domain.record.service;

import example.com.moneymergebe.domain.book.dto.response.BookGetRes;
import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.book.entity.BookRecord;
import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.book.repository.BookRecordRepository;
import example.com.moneymergebe.domain.book.repository.BookRepository;
import example.com.moneymergebe.domain.book.repository.BookUserRepository;
import example.com.moneymergebe.domain.record.dto.request.RecordCommentModifyReq;
import example.com.moneymergebe.domain.record.dto.request.RecordCommentSaveReq;
import example.com.moneymergebe.domain.record.dto.request.RecordModifyReq;
import example.com.moneymergebe.domain.record.dto.request.RecordSaveReq;
import example.com.moneymergebe.domain.record.dto.response.RecordCommentDeleteRes;
import example.com.moneymergebe.domain.record.dto.response.RecordCommentGetRes;
import example.com.moneymergebe.domain.record.dto.response.RecordCommentModifyRes;
import example.com.moneymergebe.domain.record.dto.response.RecordCommentSaveRes;
import example.com.moneymergebe.domain.record.dto.response.RecordDeleteRes;
import example.com.moneymergebe.domain.record.dto.response.RecordDislikeRes;
import example.com.moneymergebe.domain.record.dto.response.RecordGetDislikeRes;
import example.com.moneymergebe.domain.record.dto.response.RecordGetLikeRes;
import example.com.moneymergebe.domain.record.dto.response.RecordGetMonthRes;
import example.com.moneymergebe.domain.record.dto.response.RecordGetRes;
import example.com.moneymergebe.domain.record.dto.response.RecordLikeRes;
import example.com.moneymergebe.domain.record.dto.response.RecordModifyRes;
import example.com.moneymergebe.domain.record.dto.response.RecordSaveRes;
import example.com.moneymergebe.domain.record.entity.Record;
import example.com.moneymergebe.domain.record.entity.RecordComment;
import example.com.moneymergebe.domain.record.entity.RecordReaction;
import example.com.moneymergebe.domain.record.repository.RecordCommentRepository;
import example.com.moneymergebe.domain.record.repository.RecordReactionRepository;
import example.com.moneymergebe.domain.record.repository.RecordRepository;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.exception.GlobalException;
import example.com.moneymergebe.global.validator.BookRecordValidator;
import example.com.moneymergebe.global.validator.BookUserValidator;
import example.com.moneymergebe.global.validator.BookValidator;
import example.com.moneymergebe.global.validator.RecordCommentValidator;
import example.com.moneymergebe.global.validator.RecordValidator;
import example.com.moneymergebe.global.validator.UserValidator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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
    private final BookRecordRepository bookRecordRepository;
    private final RecordCommentRepository recordCommentRepository;
    private final RecordReactionRepository recordReactionRepository;

    /**
     * 레코드 생성
     */
    @Transactional
    public RecordSaveRes saveRecord(RecordSaveReq req) {
        User user = findUser(req.getUserId());
        Record record = recordRepository.save(Record.builder().date(req.getDate()).recordType(req.getRecordType()).amount(req.getAmount()).assetType(req.getAssetType())
            .content(req.getContent()).memo(req.getMemo()).image(req.getImage()).user(user).build());

        for(Long bookId : req.getBookList()) {
            Book book = findBook(bookId);
            checkBookMember(user, book); // 가계부 권한 검사
            bookRecordRepository.save(BookRecord.builder().book(book).record(record).build());
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

        List<BookRecord> bookRecordList = bookRecordRepository.findAllByBook(book);
        List<RecordGetMonthRes> resList = new ArrayList<>();
        for(BookRecord bookRecord : bookRecordList) {
            Record record = bookRecord.getRecord();
            if(!record.getDate().isBefore(startDate) && !record.getDate().isAfter(endDate)) {
                resList.add(new RecordGetMonthRes(record, bookUser));
            }
        }

        resList.sort(Comparator.comparing(RecordGetMonthRes::getDate));

        return resList;
    }

    /**
     * 레코드 상세 조회
     */
    @Transactional(readOnly = true)
    public RecordGetRes getRecord(Long userId, Long bookId, Long recordId) {
        User user = findUser(userId);
        Book book = findBook(bookId); // 현재 접속 중인 가계부
        Record record = findRecord(recordId);

        BookUser bookUser = checkBookMember(user, book); // 가계부 권한 검사
        checkBookRecord(book, record); // 가계부의 레코드인지 검사

        // 레코드 가계부 목록
        List<BookRecord> bookRecordList = bookRecordRepository.findAllByRecord(record);
        List<BookGetRes> bookGetResList = new ArrayList<>();
        for(BookRecord bookRecord : bookRecordList) {
            if(isBookMember(user, bookRecord.getBook())) { // 사용자가 멤버인 가계부만 보여줌
                bookGetResList.add(new BookGetRes(bookRecord.getBook()));
            }
        }

        // 레코드 댓글 목록
        List<RecordCommentGetRes> commentGetResList = recordCommentRepository.findAllByRecord(record).stream().map(
            recordComment -> new RecordCommentGetRes(recordComment, bookUser)
        ).toList();

        // 좋아요 개수
        int likes = recordReactionRepository.countByRecordAndReaction(record, true);
        // 싫어요 개수
        int dislikes = recordReactionRepository.countByRecordAndReaction(record, false);

        return new RecordGetRes(record, bookGetResList, commentGetResList, likes, dislikes);
    }

    /**
     * 레코드 수정
     */
    @Transactional
    public RecordModifyRes modifyRecord(RecordModifyReq req) {
        User user = findUser(req.getUserId());
        Book book = findBook(req.getBookId());
        Record record = findRecord(req.getRecordId());

        checkBookRecord(book, record); // 가계부의 레코드인지 검사
        UserValidator.checkUser(user, record.getUser()); // 작성자와 수정자가 동일한지 검사

        bookRecordRepository.deleteAllByRecord(record); // 레코드의 기존 가계부 삭제
        record.update(req); // 레코드 수정

        for(Long bookId : req.getBookList()) { // 레코드와 가계부 연관관계 설정
            Book chosenBook = findBook(bookId);
            checkBookMember(user, chosenBook); // 가계부 권한 검사
            bookRecordRepository.save(BookRecord.builder().book(chosenBook).record(record).build());
        }

        return new RecordModifyRes();
    }

    /**
     * 레코드 삭제
     */
    @Transactional
    public RecordDeleteRes deleteRecord(Long userId, Long bookId, Long recordId) {
        User user = findUser(userId);
        Book book = findBook(bookId);
        Record record = findRecord(recordId);

        checkBookRecord(book, record); // 가계부의 레코드인지 검사
        UserValidator.checkUser(user, record.getUser()); // 작성자와 삭제자가 동일한지 검사

        // bookRecord, 기록 반응, 댓글 삭제
        bookRecordRepository.deleteAllByRecord(record);
        recordReactionRepository.deleteAllByRecord(record);
        recordCommentRepository.deleteAllByRecord(record);
        // 레코드 삭제
        recordRepository.delete(record);

        return new RecordDeleteRes();
    }

    /**
     * 레코드 좋아요(토글)
     */
    @Transactional
    public RecordLikeRes likeRecord(Long userId, Long bookId, Long recordId) {
        User user = findUser(userId);
        Book book = findBook(bookId);
        Record record = findRecord(recordId);

        checkBookRecord(book, record); // 가계부의 레코드인지 검사
        checkBookMember(user, book); // 가계부 권한 검사

        RecordReaction recordReaction = recordReactionRepository.findByRecordAndUserAndReaction(record, user, true);
        if(recordReaction == null) { // 기존에 좋아요를 하지 않았으면
            recordReactionRepository.save(RecordReaction.builder().record(record).user(user).reaction(true).build()); // 좋아요 저장
        } else { // 기존에 좋아요를 했으면
            recordReactionRepository.delete(recordReaction); // 삭제
        }

        return new RecordLikeRes();
    }

    /**
     * 레코드 싫어요(토글)
     */
    @Transactional
    public RecordDislikeRes dislikeRecord(Long userId, Long bookId, Long recordId) {
        User user = findUser(userId);
        Book book = findBook(bookId);
        Record record = findRecord(recordId);

        checkBookRecord(book, record); // 가계부의 레코드인지 검사
        checkBookMember(user, book); // 가계부 권한 검사

        RecordReaction recordReaction = recordReactionRepository.findByRecordAndUserAndReaction(record, user, false);
        if(recordReaction == null) { // 기존에 싫어요를 하지 않았으면
            recordReactionRepository.save(RecordReaction.builder().record(record).user(user).reaction(false).build()); // 싫어요 저장
        } else { // 기존에 싫어요를 했으면
            recordReactionRepository.delete(recordReaction); // 삭제
        }

        return new RecordDislikeRes();
    }

    /**
     * 레코드 좋아요 수 조회
     */
    @Transactional(readOnly = true)
    public RecordGetLikeRes getRecordLike(Long userId, Long bookId, Long recordId) {
        User user = findUser(userId);
        Book book = findBook(bookId);
        Record record = findRecord(recordId);

        checkBookRecord(book, record); // 가계부의 레코드인지 검사
        checkBookMember(user, book); // 가계부 권한 검사

        return new RecordGetLikeRes(recordReactionRepository.countByRecordAndReaction(record, true));
    }

    /**
     * 레코드 싫어요 수 조회
     */
    @Transactional(readOnly = true)
    public RecordGetDislikeRes getRecordDislike(Long userId, Long bookId, Long recordId) {
        User user = findUser(userId);
        Book book = findBook(bookId);
        Record record = findRecord(recordId);

        checkBookRecord(book, record); // 가계부의 레코드인지 검사
        checkBookMember(user, book); // 가계부 권한 검사

        return new RecordGetDislikeRes(recordReactionRepository.countByRecordAndReaction(record, false));
    }

    /**
     * 레코드 댓글 생성
     */
    @Transactional
    public RecordCommentSaveRes saveRecordComment(RecordCommentSaveReq req) {
        User user = findUser(req.getUserId());
        Book book = findBook(req.getBookId());
        Record record = findRecord(req.getRecordId());

        checkBookRecord(book, record); // 가계부의 레코드인지 검사
        checkBookMember(user, book); // 가계부 권한 검사

        recordCommentRepository.save(RecordComment.builder().record(record).user(user).content(req.getContent()).build());

        return new RecordCommentSaveRes();
    }

    /**
     * 레코드 댓글 수정
     */
    @Transactional
    public RecordCommentModifyRes modifyRecordComment(RecordCommentModifyReq req) {
        User user = findUser(req.getUserId());
        Book book = findBook(req.getBookId());
        Record record = findRecord(req.getRecordId());
        RecordComment recordComment = findRecordComment(req.getCommentId());

        checkBookRecord(book, record); // 가계부의 레코드인지 검사
        RecordCommentValidator.checkRecordComment(record, recordComment.getRecord()); // 레코드의 댓글인지 검사
        UserValidator.checkUser(user, recordComment.getUser()); // 작성자와 수정자가 동일한지 검사

        recordComment.update(req.getContent());

        return new RecordCommentModifyRes();
    }

    /**
     * 레코드 댓글 삭제
     */
    @Transactional
    public RecordCommentDeleteRes deleteRecordComment(Long userId, Long bookId, Long recordId, Long commentId) {
        User user = findUser(userId);
        Book book = findBook(bookId);
        Record record = findRecord(recordId);
        RecordComment recordComment = findRecordComment(commentId);

        checkBookRecord(book, record); // 가계부의 레코드인지 검사
        RecordCommentValidator.checkRecordComment(record, recordComment.getRecord()); // 레코드의 댓글인지 검사
        UserValidator.checkUser(user, recordComment.getUser()); // 작성자와 삭제자가 동일한지 검사

        recordCommentRepository.delete(recordComment);

        return new RecordCommentDeleteRes();
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
     * @throws GlobalException recordId에 해당하는 레코드가 존재하지 않는 경우 예외 발생
     */
    private Record findRecord(Long recordId) {
        Record record = recordRepository.findByRecordId(recordId);
        RecordValidator.validate(record);
        return record;
    }

    /**
     * @throws GlobalException recordCommentId에 해당하는 레코드가 존재하지 않는 경우 예외 발생
     */
    private RecordComment findRecordComment(Long recordCommentId) {
        RecordComment recordComment = recordCommentRepository.findByRecordCommentId(recordCommentId);
        RecordCommentValidator.validate(recordComment);
        return recordComment;
    }

    /**
     * @throws GlobalException user가 book의 멤버가 아닌 경우 예외 발생
     */
    private BookUser checkBookMember(User user, Book book) {
        BookUser bookUser = bookUserRepository.findByUserAndBook(user, book);
        BookUserValidator.checkMember(bookUser);
        return bookUser;
    }

    /**
     * @throws GlobalException record가 book의 기록이 아닌 경우 예외 발생
     */
    private BookRecord checkBookRecord(Book book, Record record) {
        BookRecord bookRecord = bookRecordRepository.findByBookAndRecord(book, record);
        BookRecordValidator.checkRecord(bookRecord);
        return bookRecord;
    }

    /**
     * 사용자의 가계부 멤버 여부를 반환
     */
    private boolean isBookMember(User user, Book book) {
        BookUser bookUser = bookUserRepository.findByUserAndBook(user, book);
        return bookUser != null;
    }
}
