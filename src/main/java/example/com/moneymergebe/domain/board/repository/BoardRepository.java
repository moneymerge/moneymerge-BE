package example.com.moneymergebe.domain.board.repository;

import example.com.moneymergebe.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findByBoardId(Long boardId);
}
