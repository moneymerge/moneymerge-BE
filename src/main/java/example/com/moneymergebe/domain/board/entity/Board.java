package example.com.moneymergebe.domain.board.entity;

import example.com.moneymergebe.domain.board.dto.request.BoardModifyReq;
import example.com.moneymergebe.domain.common.BaseEntity;
import example.com.moneymergebe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_board")
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    private String image;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private int likes;



    @Builder
    private Board(BoardType boardType, String title, String content, String image, User user, int likes){
        this.boardType=boardType;
        this.title=title;
        this.content=content;
        this.image=image;
        this.user=user;
        this.likes=likes;
    }

    public void update(BoardModifyReq req) {
        this.boardType = req.getBoardType();
        this.title = req.getTitle();
        this.content = req.getContent();
        this.image = req.getImage();
    }

    public void addLike(){
        this.likes=likes+1;
    }
    public void removeLike(){
        this.likes=likes-1;
    }

}
