package example.com.moneymergebe.domain.record.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecordType {
    EXPENSE("지출"),
    INCOME("수입");
    private final String value;
}
