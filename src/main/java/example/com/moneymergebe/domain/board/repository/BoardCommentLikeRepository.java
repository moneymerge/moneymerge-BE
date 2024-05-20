package example.com.moneymergebe.domain.board.repository;

import example.com.moneymergebe.domain.board.entity.BoardComment;
import example.com.moneymergebe.domain.board.entity.BoardCommentLike;
import example.com.moneymergebe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCommentLikeRepository extends JpaRepository<BoardCommentLike, Long> {
    void deleteAllByBoardComment(BoardComment boardComment);

    BoardCommentLike findByUserAndBoardComment(User user, BoardComment boardComment);
}
