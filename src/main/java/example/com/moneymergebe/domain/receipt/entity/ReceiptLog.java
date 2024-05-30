package example.com.moneymergebe.domain.receipt.entity;

import example.com.moneymergebe.domain.user.entity.User;
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
@Table(name = "tb_receipt_log")
public class ReceiptLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long receiptLogId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "receipt_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Receipt receipt;

    @Builder
    private ReceiptLog(User user, Receipt receipt) {
        this.user = user;
        this.receipt = receipt;
    }
}
