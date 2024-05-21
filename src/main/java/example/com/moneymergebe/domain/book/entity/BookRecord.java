package example.com.moneymergebe.domain.book.entity;

import example.com.moneymergebe.domain.common.BaseEntity;
import example.com.moneymergebe.domain.record.entity.Record;
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
@Table(name = "tb_book_record")
public class BookRecord extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookRecordId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "record_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Record record;

    @Builder
    private BookRecord(Book book, Record record) {
        this.book = book;
        this.record = record;
    }
}
