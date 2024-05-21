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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_record_reaction")
public class RecordReaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordReactionId;

    @ManyToOne
    @JoinColumn(name = "record_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Record record;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false)
    private boolean reaction; // 좋아요이면 true, 싫어요이면 false

    @Builder
    private RecordReaction(Record record, User user, boolean reaction) {
        this.record = record;
        this.user = user;
        this.reaction = reaction;
    }
}
