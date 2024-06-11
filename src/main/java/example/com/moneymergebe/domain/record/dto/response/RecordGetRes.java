package example.com.moneymergebe.domain.record.dto.response;

import example.com.moneymergebe.domain.book.dto.response.BookGetRes;
import example.com.moneymergebe.domain.record.entity.Record;
import jakarta.annotation.Nullable;
import lombok.Getter;

import java.util.List;

@Getter
public class RecordGetRes {
    private Long recordId;
    private String date;
    private String recordType;
    private int amount;
    private String assetType;
    private Long categoryId;
    private String categoryName;
    private String content;
    private String memo;
    @Nullable
    private String image;
    private List<BookGetRes> bookList;
    private Long userId;
    private String username;
    private String userColor;
    private int likes;
    private int dislikes;
    private List<RecordCommentGetRes> commentList;

    public RecordGetRes(Record record, List<BookGetRes> bookGetResList, List<RecordCommentGetRes> commentGetResList, int likes, int dislikes, String userColor) {
        this.recordId = record.getRecordId();
        this.date = record.getDate().toString();
        this.recordType = record.getRecordType().getValue();
        this.amount = record.getAmount();
        this.assetType = record.getAssetType().getValue();
        this.categoryId = record.getCategory().getCategoryId();
        this.categoryName = record.getCategory().getCategory();
        this.content = record.getContent();
        this.memo = record.getMemo();
        this.image = record.getImage();
        this.bookList = bookGetResList;
        this.userId = record.getUser().getUserId();
        this.username = record.getUser().getUsername();
        this.userColor = userColor;
        this.likes = likes;
        this.dislikes = dislikes;
        this.commentList = commentGetResList;
    }
}