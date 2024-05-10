package example.com.moneymergebe.domain.book.controller;

import example.com.moneymergebe.domain.book.dto.request.*;
import example.com.moneymergebe.domain.book.dto.response.*;
import example.com.moneymergebe.domain.book.service.BookService;
import example.com.moneymergebe.global.response.CommonResponse;
import example.com.moneymergebe.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    /**
     * 가계부 저장
     */
    @PostMapping
    public CommonResponse<BookSaveRes> saveBook(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BookSaveReq req){
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(bookService.saveBook(req));
    }

    /**
     * 가계부 전체 조회
     */
    @GetMapping
    public CommonResponse<List<BookGetRes>> getAllBooks(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return CommonResponse.success(bookService.getAllBooks(userDetails.getUser().getUserId()));
    }


    /**
     * 가계부 상세 조회
     */
    @GetMapping("/{bookId}")
    public CommonResponse<BookGetRes> getBook(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId) {
        return CommonResponse.success(bookService.getBook(userDetails.getUser().getUserId(), bookId));
    }

    /**
     * 가계부 시작일 수정
     */
    @PatchMapping("/{bookId}/start-date")
    public CommonResponse<BookStartDateRes> updateStartDate(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BookStartDateReq req, @PathVariable Long bookId) {
        req.setBookId(bookId);
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(bookService.updateStartDate(req));
    }
    /**
     * 가계부 멤버 초대
     */
    @PostMapping("/{bookId}/users")
    public CommonResponse<BookUsersRes> updateUsers(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BookUsersReq req, @PathVariable Long bookId) {
        req.setBookId(bookId);
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(bookService.updateUsers(req));
    }

    /**
     * 가계부 제목 수정
     */
    @PatchMapping("/{bookId}/book-title")
    public CommonResponse<BookTitleRes> updateBookTitle(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BookTitleReq req, @PathVariable Long bookId) {
        req.setBookId(bookId);
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(bookService.updateBookTitle(req));
    }

    /**
     * 가계부 색상 수정
     */
    @PatchMapping("/{bookId}/book-color")
    public CommonResponse<BookColorRes> updateBookColor(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BookColorReq req, @PathVariable Long bookId) {
        req.setBookId(bookId);
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(bookService.updateBookColor(req));
    }

    /**
     * 가계부 내 이름 수정
     */
    @PatchMapping("/{bookId}/user-name")
    public CommonResponse<BookUsernameRes> updateUserName(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BookUsernameReq req, @PathVariable Long bookId) {
        req.setBookId(bookId);
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(bookService.updateUsername(req));
    }

    /**
     * 가계부 내 색상
     */
    @PatchMapping("/{bookId}/user-color")
    public CommonResponse<BookUserColorRes> updateUserColor(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BookUserColorReq req, @PathVariable Long bookId) {
        req.setBookId(bookId);
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(bookService.updateUserColor(req));
    }

    /**
     * 가계부 올해 목표 수정
     */
    @PatchMapping("/{bookId}/year-goal")
    public CommonResponse<BookYearGoalRes> updateBookTitle(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BookYearGoalReq req, @PathVariable Long bookId) {
        req.setBookId(bookId);
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(bookService.updateYearGoal(req));
    }

    /**
     * 가계부 이번달 목표 수정
     */
    @PatchMapping("/{bookId}/month-goal")
    public CommonResponse<BookMonthGoalRes> updateMonthGoal(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BookMonthGoalReq req, @PathVariable Long bookId) {
        req.setBookId(bookId);
        req.setUserId(userDetails.getUser().getUserId());
        return CommonResponse.success(bookService.updateMonthGoal(req));
    }

    /**
     * 가계부 삭제 동의
     */
    @PatchMapping("/{bookId}/agree")
    public CommonResponse<BookDeleteAgreeRes> deleteAgree(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId) {
        return CommonResponse.success(bookService.deleteAgree(userDetails.getUser().getUserId(), bookId));
    }

    /**
     * 가계부 삭제
     */
    @DeleteMapping("/{bookId}")
    public CommonResponse<BookDeleteRes> deleteBook(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long bookId) {
        return CommonResponse.success(bookService.deleteBook(userDetails.getUser().getUserId(), bookId));
    }
}
