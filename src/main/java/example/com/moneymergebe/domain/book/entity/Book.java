package example.com.moneymergebe.domain.book.entity;

import example.com.moneymergebe.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_book")
public class Book extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long yearGoal;

    @Column(nullable = false)
    private Long monthGoal;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private int startDate;

    @OneToMany(mappedBy = "book")
    private List<BookUser> bookUserList = new ArrayList<>();

    @Builder
    private Book(String title, Long yearGoal, Long monthGoal, String color, int startDate) {
        this.title = title;
        this.yearGoal = yearGoal;
        this.monthGoal = monthGoal;
        this.color = color;
        this.startDate = startDate;
    }

    public void updateStartDate(int startDate) {
        this.startDate=startDate;
    }
    public void updateBookTitle(String bookTitle) {
        this.title=bookTitle;
    }
    public void updateBookColor(String bookColor) {
        this.color=bookColor;
    }

    public void updateYearGoal(Long yearGoal) {
        this.yearGoal=yearGoal;
    }
    public void updateMonthGoal(Long monthGoal) {
        this.monthGoal=monthGoal;
    }

}