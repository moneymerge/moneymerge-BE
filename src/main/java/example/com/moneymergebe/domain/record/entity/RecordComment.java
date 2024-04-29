package example.com.moneymergebe.domain.record.entity;

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
@Table(name = "tb_record_comment")
public class RecordComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordCommentId;

    @ManyToOne
    @JoinColumn(name = "record_id")
    private Record record;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String content;

    @Builder
    private RecordComment(Record record, User user, String content) {
        this.record = record;
        this.user = user;
        this.content = content;
    }

    public void update(String content) {
        this.content = content;
    }
}
