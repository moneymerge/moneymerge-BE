package example.com.moneymergebe.domain.board.repository;

import example.com.moneymergebe.domain.board.entity.Board;
import example.com.moneymergebe.domain.board.entity.BoardLike;
import example.com.moneymergebe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    void deleteAllByBoard(Board board);

    BoardLike findByUserAndBoard(User user, Board board);
}
