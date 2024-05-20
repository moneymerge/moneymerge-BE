package example.com.moneymergebe.domain.board.controller;

import example.com.moneymergebe.domain.board.dto.request.BoardCommentModifyReq;
import example.com.moneymergebe.domain.board.dto.request.BoardCommentSaveReq;
import example.com.moneymergebe.domain.board.dto.request.BoardModifyReq;
import example.com.moneymergebe.domain.board.dto.request.BoardSaveReq;
import example.com.moneymergebe.domain.board.dto.response.*;
import example.com.moneymergebe.domain.board.entity.BoardType;
import example.com.moneymergebe.domain.board.service.BoardService;
import example.com.moneymergebe.global.response.CommonResponse;
import example.com.moneymergebe.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {
    private final BoardService boardService;

    private static final String DEFAULT_PAGE = "1";
    private static final String DEFAULT_SIZE = "10";
    private static final String DEFAULT_SORT_BY = "createdAt";
    private static final String DEFAULT_IS_ASC = "false";
    private static final String DEFAULT_RANGE = "titleAndContent";

    /**
     * 게시글 생성
     */
    @PostMapping
    public CommonResponse<BoardSaveRes> saveBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody BoardSaveReq req,
            @RequestPart MultipartFile multipartFile) {
        req.setUserId(userDetails.getUser().getUserId());
        req.setImage(multipartFile);
        return CommonResponse.success(boardService.saveBoard(req));
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/{boardId}")
    public CommonResponse<BoardGetRes> getBoard(@PathVariable Long boardId) {

        return CommonResponse.success(boardService.getBoard(boardId));
    }

    /**
     * 게시글 전체 조회
     */
    @GetMapping
    public CommonResponse<List<BoardGetRes>> getAllBoards(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "size", defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY) String sortBy,
            @RequestParam(value = "isAsc", defaultValue = DEFAULT_IS_ASC) boolean isAsc,
            @RequestParam(required = false) BoardType boardType) {
        if (boardType != null) {
            return CommonResponse.success(boardService.getAllBoardsByBoardType(page -1, size, sortBy, isAsc, boardType)); // 특정 게시판 게시글 전체 조회
        } else {
            return CommonResponse.success(boardService.getAllBoards(page -1, size, sortBy, isAsc)); // 게시글 전체 조회
        }
    }


    /**
     * 게시글 수정
     */
    @PutMapping("/{boardId}")
    public CommonResponse<BoardModifyRes> modifyBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long boardId,
            @RequestBody BoardModifyReq req,
            @RequestPart MultipartFile multipartFile) {
        req.setUserId(userDetails.getUser().getUserId());
        req.setBoardId(boardId);
        req.setImage(multipartFile);
        return CommonResponse.success(boardService.modifyBoard(req));
    }


    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{boardId}")
    public CommonResponse<BoardDeleteRes> deleteBoard(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @PathVariable Long boardId) {
        return CommonResponse.success(boardService.deleteBoard(userDetails.getUser().getUserId(), boardId));
    }

    /**
     * 게시글 좋아요(토글)
     */
    @PostMapping("/{boardId}/likes")
    public CommonResponse<BoardLikeRes> likeBoard(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @PathVariable Long boardId) {
        return CommonResponse.success(boardService.likeBoard(userDetails.getUser().getUserId(), boardId));
    }

    /**
     * 게시글 댓글 생성
     */
    @PostMapping("/{boardId}/comments")
    public CommonResponse<BoardCommentSaveRes> saveBoardComment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                  @PathVariable Long boardId, @RequestBody BoardCommentSaveReq req) {
        req.setUserId(userDetails.getUser().getUserId());
        req.setBoardId(boardId);
        return CommonResponse.success(boardService.saveBoardComment(req));
    }


    /**
     * 게시글 댓글 수정
     */
    @PutMapping("/{boardId}/comments/{commentId}")
    public CommonResponse<BoardCommentModifyRes> modifyBoardComment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                      @PathVariable Long boardId, @PathVariable Long commentId, @RequestBody BoardCommentModifyReq req) {
        req.setUserId(userDetails.getUser().getUserId());
        req.setBoardId(boardId);
        req.setCommentId(commentId);
        return CommonResponse.success(boardService.modifyRecordComment(req));
    }

    /**
     * 게시글 댓글 삭제
     */
    @DeleteMapping("/{boardId}/comments/{commentId}")
    public CommonResponse<BoardCommentDeleteRes> deleteBoardComment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                      @PathVariable Long boardId, @PathVariable Long commentId) {
        return CommonResponse.success(boardService.deleteBoardComment(userDetails.getUser().getUserId(), boardId, commentId));
    }

    /**
     * 게시글 댓글 좋아요(토글)
     */
    @PostMapping("/{boardId}/comments/{commentId}/likes")
    public CommonResponse<BoardCommentLikeRes> likeBoardComment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @PathVariable Long boardId, @PathVariable Long commentId) {
        return CommonResponse.success(boardService.likeBoardComment(userDetails.getUser().getUserId(), boardId, commentId));
    }

    /**
     * 게시글 검색 기능
     */
    @GetMapping("/search")
    public CommonResponse<List<BoardGetRes>> searchBoard(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "size", defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY) String sortBy,
            @RequestParam(value = "isAsc", defaultValue = DEFAULT_IS_ASC) boolean isAsc,
            @RequestParam(value = "range", defaultValue = DEFAULT_RANGE) String range,
            @RequestParam(required = false) String searchKeyword){
        return CommonResponse.success(boardService.searchBoard(page - 1, size, sortBy, isAsc, range, searchKeyword));
    }
}
