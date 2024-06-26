package example.com.moneymergebe.domain.board.entity;

import example.com.moneymergebe.domain.common.BaseEntity;
import example.com.moneymergebe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardCommentId;

    @ManyToOne
    @JoinColumn(name = "board_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int likes;

    @Builder
    private BoardComment(Board board, User user, String content, int likes) {
        this.board = board;
        this.user = user;
        this.content = content;
        this.likes = likes;
    }

    public void update(String content) {
        this.content = content;
    }

    public void addLike(){
        this.likes=likes+1;
    }
    public void removeLike(){
        this.likes=likes-1;
    }
}
