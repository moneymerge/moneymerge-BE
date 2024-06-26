package example.com.moneymergebe.domain.board.service;

import static example.com.moneymergebe.global.response.ResultCode.NOT_FOUND_RANGE;

import example.com.moneymergebe.domain.board.dto.request.BoardCommentModifyReq;
import example.com.moneymergebe.domain.board.dto.request.BoardCommentSaveReq;
import example.com.moneymergebe.domain.board.dto.request.BoardModifyReq;
import example.com.moneymergebe.domain.board.dto.request.BoardSaveReq;
import example.com.moneymergebe.domain.board.dto.response.*;
import example.com.moneymergebe.domain.board.entity.*;
import example.com.moneymergebe.domain.board.repository.BoardCommentLikeRepository;
import example.com.moneymergebe.domain.board.repository.BoardCommentRepository;
import example.com.moneymergebe.domain.board.repository.BoardLikeRepository;
import example.com.moneymergebe.domain.board.repository.BoardRepository;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.exception.GlobalException;
import example.com.moneymergebe.global.response.ResultCode;
import example.com.moneymergebe.global.validator.BoardCommentValidator;
import example.com.moneymergebe.global.validator.BoardValidator;
import example.com.moneymergebe.global.validator.UserValidator;
import example.com.moneymergebe.infra.s3.S3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardCommentLikeRepository boardCommentLikeRepository;
    private final S3Util s3Util;

    /**
     * 게시글 생성
     */
    @Transactional
    public BoardSaveRes saveBoard(BoardSaveReq req, MultipartFile multipartFile) {
        User user = findUser(req.getUserId());
        String imageUrl = null;
        imageUrl = s3Util.uploadFile(multipartFile, S3Util.FilePath.BOARD); // 업로드 후 게시글 사진으로 설정


        Board board = boardRepository.save(Board.builder().boardType(req.getBoardType()).title(req.getTitle()).content(req.getContent()).image(imageUrl).user(user).likes(0).build());

        return new BoardSaveRes();
    }

    /**
     * 게시글 상세 조회
     */
    @Transactional(readOnly = true)
    public BoardGetDetailRes getBoard(Long boardId) {
        Board board = findBoard(boardId);

        // 게시글 댓글 목록
        List<BoardCommentGetRes> commentGetResList = boardCommentRepository.findAllByBoard(board)
                .stream().map(
                        boardComment -> new BoardCommentGetRes(boardComment, boardComment.getUser().getProfileUrl())
                ).toList();

        return new BoardGetDetailRes(board, commentGetResList);
    }

    /**
     * 게시글 전체 조회
     */
    @Transactional(readOnly = true)
    public Page<BoardGetRes> getAllBoards(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return boardRepository.findAll(pageable).map(board -> new BoardGetRes(board, boardCommentRepository.countByBoard(board)));
    }

    /**
     * 특정 게시판 게시글 전체 조회
     */
    @Transactional(readOnly = true)
    public Page<BoardGetRes> getAllBoardsByBoardType(int page, int size, String sortBy, boolean isAsc, BoardType boardType) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return boardRepository.findAllByBoardType(pageable, boardType).map(board -> new BoardGetRes(board, boardCommentRepository.countByBoard(board)));
    }



    /**
     * 게시글 수정
     */
    @Transactional
    public BoardModifyRes modifyBoard(BoardModifyReq req) {
        User user = findUser(req.getUserId());
        Board board = findBoard(req.getBoardId());

        UserValidator.checkUser(user, board.getUser()); // 작성자와 수정자가 동일한지 검사

        String imageUrl = board.getImage();
        if (req.getImage() != null && !req.getImage().isEmpty()) { // 새로 입력한 이미지 파일이 있는 경우
            if (imageUrl != null && s3Util.exists(imageUrl, S3Util.FilePath.BOARD)) { // 기존 이미지가 존재하는 경우
                s3Util.deleteFile(imageUrl, S3Util.FilePath.BOARD); // 기존 이미지 삭제
            }
            imageUrl = s3Util.uploadFile(req.getImage(), S3Util.FilePath.BOARD); // 업로드 후 게시글 사진으로 설정
        }

        board.update(req, imageUrl); // 게시글 수정

        return new BoardModifyRes();
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public BoardDeleteRes deleteBoard(Long userId, Long boardId) {
        User user = findUser(userId);
        Board board = findBoard(boardId);

        UserValidator.checkUser(user, board.getUser()); // 작성자와 삭제자가 동일한지 검사

        // 게시글 삭제
        boardRepository.delete(board);

        return new BoardDeleteRes();
    }

    /**
     * 게시글 좋아요(토글)
     */
    @Transactional
    public BoardLikeRes likeBoard(Long userId, Long boardId) {
        User user = findUser(userId);
        Board board = findBoard(boardId);

        BoardLike boardLike = boardLikeRepository.findByUserAndBoard(user, board);
        if(boardLike == null) {
            board.addLike();
            boardLikeRepository.save(BoardLike.builder().user(user).board(board).build());

        }
        else {
            board.removeLike();
            boardLikeRepository.delete(boardLike);
        }

        return new BoardLikeRes(board.getLikes());
    }

    /**
     * 게시글 댓글 생성
     */
    @Transactional
    public BoardCommentSaveRes saveBoardComment(BoardCommentSaveReq req) {
        User user = findUser(req.getUserId());
        Board board = findBoard(req.getBoardId());

        boardCommentRepository.save(BoardComment.builder().board(board).user(user).content(req.getContent()).likes(0).build());

        return new BoardCommentSaveRes();
    }

    /**
     * 게시글 댓글 수정
     */
    @Transactional
    public BoardCommentModifyRes modifyRecordComment(BoardCommentModifyReq req) {
        User user = findUser(req.getUserId());
        Board board = findBoard(req.getBoardId());
        BoardComment boardComment = findBoardComment(req.getCommentId());

        BoardCommentValidator.checkBoardComment(board, boardComment.getBoard()); // 게시글의 댓글인지 검사
        UserValidator.checkUser(user, boardComment.getUser()); // 작성자와 수정자가 동일한지 검사

        boardComment.update(req.getContent());

        return new BoardCommentModifyRes();
    }

    /**
     * 게시글 댓글 삭제
     */
    @Transactional
    public BoardCommentDeleteRes deleteBoardComment(Long userId, Long boardId, Long commentId) {
        User user = findUser(userId);
        Board board = findBoard(boardId);
        BoardComment boardComment = findBoardComment(commentId);

        BoardCommentValidator.checkBoardComment(board, boardComment.getBoard());
        UserValidator.checkUser(user, boardComment.getUser());

        // 게시글 댓글 삭제
        boardCommentRepository.delete(boardComment);

        return new BoardCommentDeleteRes();
    }

    /**
     * 게시글 댓글 좋아요(토글)
     */
    @Transactional
    public BoardCommentLikeRes likeBoardComment(Long userId, Long boardId, Long commentId) {
        User user = findUser(userId);
        Board board = findBoard(boardId);
        BoardComment boardComment = findBoardComment(commentId);

        BoardCommentLike boardCommentLike = boardCommentLikeRepository.findByUserAndBoardComment(user, boardComment);
        if(boardCommentLike == null) {
            boardComment.addLike();
            boardCommentLikeRepository.save(BoardCommentLike.builder().user(user).boardComment(boardComment).build());
        }
        else {
            boardComment.removeLike();
            boardCommentLikeRepository.delete(boardCommentLike);
        }

        return new BoardCommentLikeRes(boardComment.getLikes());
    }

    /**
     * 게시글 검색 기능
     */
    @Transactional
    public Page<BoardGetRes> searchBoard(int page, int size, String sortBy, boolean isAsc, String range, BoardType boardType, String searchKeyword) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Board> boardList = null;
        switch (range) {
            case "titleAndContent":
                boardList = boardRepository.findByBoardTypeAndTitleContainingOrContentContaining(pageable, boardType, searchKeyword, searchKeyword);
                log.info(range);
                break;
            case "title":
                boardList = boardRepository.findByBoardTypeAndTitleContaining(pageable, boardType, searchKeyword);
                break;
            case "content":
                boardList = boardRepository.findByBoardTypeAndContentContaining(pageable, boardType, searchKeyword);
                break;
            case "user":
                User user = userRepository.findByUsername(searchKeyword);
                boardList = boardRepository.findByBoardTypeAndUser(pageable, boardType, user);
                break;
            default:
                throw new GlobalException(NOT_FOUND_RANGE);
        }

        return boardList.map(board -> new BoardGetRes(board, boardCommentRepository.countByBoard(board)));
    }

    @Transactional
    public Page<BoardGetRes> searchBoard(int page, int size, String sortBy, boolean isAsc, String range, String searchKeyword) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Board> boardList = null;
        switch (range) {
            case "titleAndContent":
                boardList = boardRepository.findByTitleContainingOrContentContaining(pageable, searchKeyword, searchKeyword);
                log.info(range);
                break;
            case "title":
                boardList = boardRepository.findByTitleContaining(pageable, searchKeyword);
                break;
            case "content":
                boardList = boardRepository.findByContentContaining(pageable, searchKeyword);
                break;
            case "user":
                User user = userRepository.findByUsername(searchKeyword);
                boardList = boardRepository.findByUser(pageable, user);
                break;
            default:
                throw new IllegalArgumentException("유효한 range값이 아닙니다.");
        }

        return boardList.map(board -> new BoardGetRes(board, boardCommentRepository.countByBoard(board)));
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
     * @throws GlobalException boardId에 해당하는 가계부가 존재하지 않는 경우 예외 발생
     */
    private Board findBoard (Long boardId){
        Board board = boardRepository.findByBoardId(boardId);
        BoardValidator.validate(board);
        return board;
    }

    /**
     * @throws GlobalException boardCommentId에 해당하는 게시글 댓글이 존재하지 않는 경우 예외 발생
     */
    private BoardComment findBoardComment(Long boardCommentId) {
        BoardComment boardComment = boardCommentRepository.findByBoardCommentId(boardCommentId);
        BoardCommentValidator.validate(boardComment);
        return boardComment;
    }
}
