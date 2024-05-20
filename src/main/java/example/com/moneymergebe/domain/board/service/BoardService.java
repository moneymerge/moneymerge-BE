package example.com.moneymergebe.domain.board.service;

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

import java.util.ArrayList;
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

    private static final String IMAGE_JPG = "image/jpeg";
    private static final String IMAGE_PNG = "image/png";

    /**
     * 게시글 생성
     */
    @Transactional
    public BoardSaveRes saveBoard(BoardSaveReq req) {
        User user = findUser(req.getUserId());

        String imageUrl = null;
        if (req.getImage() != null && !req.getImage().isEmpty()) { // 새로 입력한 이미지 파일이 있는 경우
            checkImage(req.getImage()); // 이미지 파일인지 확인
            imageUrl = s3Util.uploadFile(req.getImage(), S3Util.FilePath.BOARD); // 업로드 후 게시글 사진으로 설정
        }

        Board board = boardRepository.save(Board.builder().boardType(req.getBoardType()).title(req.getTitle()).content(req.getContent()).image(imageUrl).user(user).likes(0).build());

        return new BoardSaveRes();
    }

    /**
     * 게시글 상세 조회
     */
    @Transactional(readOnly = true)
    public BoardGetRes getBoard(Long boardId) {
        Board board = findBoard(boardId);

        // 게시글 댓글 목록
        List<BoardCommentGetRes> commentGetResList = boardCommentRepository.findAllByBoard(board)
                .stream().map(
                        boardComment -> new BoardCommentGetRes(boardComment)
                ).toList();

        return new BoardGetRes(board, commentGetResList);
    }

    /**
     * 게시글 전체 조회
     */
    @Transactional(readOnly = true)
    public List<BoardGetRes> getAllBoards(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Board> boardList = boardRepository.findAll(pageable);
        List<BoardGetRes> boardGetResList = new ArrayList<>();
        for (Board board : boardList) {

            List<BoardCommentGetRes> commentGetResList = boardCommentRepository.findAllByBoard(board)
                    .stream().map(
                            boardComment -> new BoardCommentGetRes(boardComment)
                    ).toList();

            boardGetResList.add(new BoardGetRes(board, commentGetResList));
        }
        return boardGetResList;
    }

    /**
     * 특정 게시판 게시글 전체 조회
     */
    @Transactional(readOnly = true)
    public List<BoardGetRes> getAllBoardsByBoardType(int page, int size, String sortBy, boolean isAsc, BoardType boardType) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Board> boardList = boardRepository.findAllByBoardType(pageable, boardType);
        List<BoardGetRes> boardGetResList = new ArrayList<>();
        for (Board board : boardList) {

            List<BoardCommentGetRes> commentGetResList = boardCommentRepository.findAllByBoard(board)
                    .stream().map(
                            boardComment -> new BoardCommentGetRes(boardComment)
                    ).toList();

            boardGetResList.add(new BoardGetRes(board, commentGetResList));
        }
        return boardGetResList;
    }



    /**
     * 게시글 수정
     */
    @Transactional
    public BoardModifyRes modifyBoard(BoardModifyReq req) {
        User user = findUser(req.getUserId());
        Board board = findBoard(req.getBoardId());

        UserValidator.checkUser(user, board.getUser()); // 작성자와 수정자가 동일한지 검사

        String imageUrl = board.getImage(); // 기존 프로필 이미지
        if (req.getImage() != null && !req.getImage().isEmpty()) { // 새로 입력한 이미지 파일이 있는 경우
            if (s3Util.exists(imageUrl, S3Util.FilePath.BOARD)) { // 기존 이미지가 존재하는 경우
                s3Util.deleteFile(imageUrl, S3Util.FilePath.BOARD); // 기존 이미지 삭제
            }
            checkImage(req.getImage()); // 이미지 파일인지 확인
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

        // 게시글 댓글 좋아요 삭제
        List<BoardComment> boardCommentList = boardCommentRepository.findAllByBoard(board);
        for(BoardComment boardComment : boardCommentList){
            boardCommentLikeRepository.deleteAllByBoardComment(boardComment);
        }
        // 게시글 댓글 삭제
        boardCommentRepository.deleteAllByBoard(board);
        // 게시글 좋아요 삭제
        boardLikeRepository.deleteAllByBoard(board);
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

        return new BoardLikeRes();
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
        BoardComment boardComment = findBoardComment(req.getBoardId());

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

        // 게시글 댓글 좋아요 삭제
        boardCommentLikeRepository.deleteAllByBoardComment(boardComment);
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

        return new BoardCommentLikeRes();
    }

    /**
     * 게시글 검색 기능
     */
    @Transactional
    public List<BoardGetRes> searchBoard(int page, int size, String sortBy, boolean isAsc, String range, String searchKeyword) {
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
        List<BoardGetRes> boardGetResList = new ArrayList<>();
        for (Board board : boardList) {

            List<BoardCommentGetRes> commentGetResList = boardCommentRepository.findAllByBoard(board)
                    .stream().map(
                            boardComment -> new BoardCommentGetRes(boardComment)
                    ).toList();

            boardGetResList.add(new BoardGetRes(board, commentGetResList));
        }
        return boardGetResList;
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

    private void checkImage(MultipartFile multipartFile) {
        String fileType = multipartFile.getContentType();

        if(fileType == null || (!fileType.equals(IMAGE_JPG) && !fileType.equals(IMAGE_PNG))) {
            throw new GlobalException(ResultCode.INVALID_IMAGE_FILE);
        }
    }
}
