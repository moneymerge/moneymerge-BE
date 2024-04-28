package example.com.moneymergebe.domain.receipt.entity;

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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_receipt")
public class Receipt extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long receiptId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean shared;

    @Column(nullable = false)
    private int positive;

    @Column(nullable = false)
    private int negative;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    private Receipt(LocalDate date, String content, boolean shared, int positive, int negative, User user) {
        this.date = date;
        this.content = content;
        this.shared = shared;
        this.positive = positive;
        this.negative = negative;
        this.user = user;
    }
}
