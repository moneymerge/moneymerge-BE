package example.com.moneymergebe.domain.board.entity;

import example.com.moneymergebe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_board_comment_like")
public class BoardCommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardCommentLikeId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "board_comment_id")
    private BoardComment boardComment;

    @Builder
    private BoardCommentLike(User user, BoardComment boardComment) {
        this.user = user;
        this.boardComment = boardComment;
    }
}
