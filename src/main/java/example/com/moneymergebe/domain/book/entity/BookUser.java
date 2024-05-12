package example.com.moneymergebe.domain.book.entity;

import example.com.moneymergebe.domain.common.BaseEntity;
import example.com.moneymergebe.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_book_user")
public class BookUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookUserId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private boolean deleteAgree;

    @Column(nullable = false)
    private String name; // 가계부 별 닉네임

    @Builder
    private BookUser(Book book, User user){
        this.book=book;
        this.user=user;
        this.color="red"; //색상 랜덤 코드
        this.deleteAgree=false;
        this.name=user.getUsername();
    }
    public void updateUsername(String username){
        this.name=username;
    }
    public void updateUserColor(String userColor){
        this.color=userColor;
    }
    public void updateDeleteAgree(){
        if(this.deleteAgree==false){
            this.deleteAgree=true;
        } else {
            this.deleteAgree=false;
        }
    }

}

