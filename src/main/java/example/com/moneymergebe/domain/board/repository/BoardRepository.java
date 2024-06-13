package example.com.moneymergebe.domain.board.repository;

import example.com.moneymergebe.domain.board.entity.Board;
import example.com.moneymergebe.domain.board.entity.BoardType;
import example.com.moneymergebe.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findByBoardId(Long boardId);
    Long countAllBy();
    Long countAllByBoardType(BoardType boardType);
    Page<Board> findAllByBoardType(Pageable pageable, BoardType boardType);

    Page<Board> findByTitleContaining(Pageable pageable, String searchKeyword);
    Page<Board> findByContentContaining(Pageable pageable, String searchKeyword);
    Page<Board> findByUser(Pageable pageable, User user);

    Page<Board> findByTitleContainingOrContentContaining(Pageable pageable, String title, String content);

    Page<Board> findByBoardTypeAndTitleContainingOrContentContaining(Pageable pageable, BoardType boardType, String searchKeyword, String searchKeyword1);

    Page<Board> findByBoardTypeAndTitleContaining(Pageable pageable, BoardType boardType, String searchKeyword);

    Page<Board> findByBoardTypeAndContentContaining(Pageable pageable, BoardType boardType, String searchKeyword);

    Page<Board> findByBoardTypeAndUser(Pageable pageable, BoardType boardType, User user);
}
