package example.com.moneymergebe.domain.board.repository;

import example.com.moneymergebe.domain.board.entity.Board;
import example.com.moneymergebe.domain.board.entity.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findByBoardId(Long boardId);
    List<Board> findAllByBoardType(BoardType boardType);
}
