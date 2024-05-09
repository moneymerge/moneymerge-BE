package example.com.moneymergebe.global.validator;

import example.com.moneymergebe.domain.board.entity.Board;
import example.com.moneymergebe.domain.board.entity.BoardComment;
import example.com.moneymergebe.global.exception.GlobalException;

import static example.com.moneymergebe.global.response.ResultCode.*;

public class BoardCommentValidator {

    public static void validate(BoardComment boardComment){
        if(checkIsNull(boardComment)) throw new GlobalException(NOT_FOUND_BOARD_COMMENT);
    }

    public static void checkBoardComment(Board savedBoard, Board board) {
        if(!savedBoard.getBoardId().equals(board.getBoardId())) throw new GlobalException(UNMATCHED_BOARD_COMMENT);
    }

    private static boolean checkIsNull(BoardComment boardComment) {
        return boardComment == null;
    }
}
