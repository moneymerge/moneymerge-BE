package example.com.moneymergebe.domain.record.entity;

import example.com.moneymergebe.domain.book.entity.Book;
import example.com.moneymergebe.domain.common.BaseEntity;
import example.com.moneymergebe.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "tb_record")
public class Record extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @Column(nullable = false)
    private LocalDate date; // 등록일

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecordType recordType; // 수입, 지출

    @Column(nullable = false)
    private int amount; // 금액

    @Column(nullable = false)
    private AssetType assetType; // 카드, 현금, 계좌

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String memo;

    @Column
    private String image;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

//    @ManyToOne
//    @JoinColumn(name = "category_id")
//    private Category category;


    @Builder
    private Record(LocalDate date, RecordType recordType, int amount, AssetType assetType,
        String content, String memo, String image, User user) {
        this.date = date;
        this.recordType = recordType;
        this.amount = amount;
        this.assetType = assetType;
        this.content = content;
        this.memo = memo;
        this.image = image;
        this.user = user;
    }
}
