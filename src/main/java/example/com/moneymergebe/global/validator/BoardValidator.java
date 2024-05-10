package example.com.moneymergebe.global.validator;

import example.com.moneymergebe.domain.board.entity.Board;
import example.com.moneymergebe.global.exception.GlobalException;
import static example.com.moneymergebe.global.response.ResultCode.NOT_FOUND_BOARD;

public class BoardValidator {
    public static void validate(Board board){
        if(checkIsNull(board)) throw new GlobalException(NOT_FOUND_BOARD);
    }

    private static boolean checkIsNull(Board board) {
        return board == null;
    }
}
