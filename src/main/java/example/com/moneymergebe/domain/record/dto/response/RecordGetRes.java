package example.com.moneymergebe.domain.record.dto.response;

import example.com.moneymergebe.domain.book.dto.response.BookGetRes;
import example.com.moneymergebe.domain.record.entity.AssetType;
import example.com.moneymergebe.domain.record.entity.Record;
import example.com.moneymergebe.domain.record.entity.RecordType;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;

@Getter
public class RecordGetRes {
    private Long recordId;
    private LocalDate date;
    private RecordType recordType;
    private int amount;
    private AssetType assetType;
    // TODO: 카테고리 추가
    private String content;
    private String memo;
    @Nullable
    private String image;
    private List<BookGetRes> bookList;
    private Long userId;
    private String username;
    private int likes;
    private int dislikes;
    private List<RecordCommentGetRes> commentList;

    public RecordGetRes(Record record, List<BookGetRes> bookGetResList, List<RecordCommentGetRes> commentGetResList, int likes, int dislikes) {
        this.recordId = record.getRecordId();
        this.date = record.getDate();
        this.recordType = record.getRecordType();
        this.amount = record.getAmount();
        this.assetType = record.getAssetType();
        this.content = record.getContent();
        this.memo = record.getMemo();
        this.image = record.getImage();
        this.bookList = bookGetResList;
        this.userId = record.getUser().getUserId();
        this.username = record.getUser().getUsername();
        this.likes = likes;
        this.dislikes = dislikes;
        this.commentList = commentGetResList;
    }
}