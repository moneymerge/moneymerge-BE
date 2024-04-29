package example.com.moneymergebe.domain.book.controller;

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
import example.com.moneymergebe.domain.book.service.BookService;
import example.com.moneymergebe.global.response.CommonResponse;
import example.com.moneymergebe.global.security.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/api/test/books")
    public String test(){
        return "get test";
    }

    /**
     * 가계부 저장
     */
    @PostMapping("/api/books")
    public CommonResponse<BookSaveRes> saveBook(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BookSaveReq req){
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(bookService.saveBook(req));
    }

    /**
     * 가계부 전체 조회
     */
    @GetMapping("/api/books")
    public CommonResponse<List<BookGetRes>> getAllBooks(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return CommonResponse.success(bookService.getAllBooks(userDetails.getUser().getUserId()));
    }


    /**
     * 가계부 상세 조회
     */
    @GetMapping("/api/books/{bookId}")
    public CommonResponse<BookGetRes> getBook(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId) {
        return CommonResponse.success(bookService.getBook(userDetails.getUser().getUserId(), bookId));
    }

    /**
     * 가계부 시작일 수정
     */
    @PatchMapping("/api/books/{bookId}/start-date")
    public CommonResponse<BookStartDateRes> updateStartDate(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BookStartDateReq req, @PathVariable Long bookId) {
        req.setBookId(bookId);
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(bookService.updateStartDate(req));
    }
    /**
     * 가계부 멤버 초대
     */
    @PatchMapping("/api/books/{bookId}/users")
    public CommonResponse<BookUsersRes> updateUsers(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BookUsersReq req, @PathVariable Long bookId) {
        req.setBookId(bookId);
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(bookService.updateUsers(req));
    }

    /**
     * 가계부 제목 수정
     */
    @PatchMapping("/api/books/{bookId}/book-title")
    public CommonResponse<BookTitleRes> updateBookTitle(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BookTitleReq req, @PathVariable Long bookId) {
        req.setBookId(bookId);
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(bookService.updateBookTitle(req));
    }

    /**
     * 가계부 색상 수정
     */
    @PatchMapping("/api/books/{bookId}/book-color")
    public CommonResponse<BookColorRes> updateBookColor(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BookColorReq req, @PathVariable Long bookId) {
        req.setBookId(bookId);
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(bookService.updateBookColor(req));
    }

    /**
     * 가계부 내 이름 수정
     */
    @PatchMapping("/api/books/{bookId}/user-name")
    public CommonResponse<BookUsernameRes> updateUserName(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BookUsernameReq req, @PathVariable Long bookId) {
        req.setBookId(bookId);
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(bookService.updateUsername(req));
    }

    /**
     * 가계부 내 색상
     */
    @PatchMapping("/api/books/{bookId}/user-color")
    public CommonResponse<BookUserColorRes> updateUserColor(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BookUserColorReq req, @PathVariable Long bookId) {
        req.setBookId(bookId);
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(bookService.updateUserColor(req));
    }

    /**
     * 가계부 올해 목표 수정
     */
    @PatchMapping("/api/books/{bookId}/year-goal")
    public CommonResponse<BookYearGoalRes> updateBookTitle(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BookYearGoalReq req, @PathVariable Long bookId) {
        req.setBookId(bookId);
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(bookService.updateYearGoal(req));
    }

    /**
     * 가계부 이번달 목표 수정
     */
    @PatchMapping("/api/books/{bookId}/month-goal")
    public CommonResponse<BookMonthGoalRes> updateMonthGoal(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BookMonthGoalReq req, @PathVariable Long bookId) {
        req.setBookId(bookId);
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(bookService.updateMonthGoal(req));
    }

    /**
     * 가계부 삭제 동의
     */
    @PatchMapping("/api/books/{bookId}/agree")
    public CommonResponse<BookDeleteAgreeRes> deleteAgree(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId) {
        return CommonResponse.success(bookService.deleteAgree(userDetails.getUser().getUserId(), bookId));
    }

    /**
     * 가계부 삭제
     */
    @DeleteMapping("/api/books/{bookId}")
    public CommonResponse<BookDeleteRes> deleteBook(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId) {
        return CommonResponse.success(bookService.deleteBook(userDetails.getUser().getUserId(), bookId));
    }
}
