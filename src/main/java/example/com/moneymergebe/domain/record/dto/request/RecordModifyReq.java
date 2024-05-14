package example.com.moneymergebe.domain.record.dto.request;

import example.com.moneymergebe.domain.record.entity.AssetType;
import example.com.moneymergebe.domain.record.entity.RecordType;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecordModifyReq {
    private Long bookId; // 현재 작성하는 가계부 ID
    private Long recordId;
    private LocalDate date;
    private RecordType recordType;
    private int amount;
    private AssetType assetType;
    private String content;
    private String memo;
    @Nullable
    private String image;
    private Long userId;
    private Long[] bookList; // 현재 가계부 ID 포함
    private Long categoryId;
}
