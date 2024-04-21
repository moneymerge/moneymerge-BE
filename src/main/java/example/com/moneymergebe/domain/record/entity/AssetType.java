package example.com.moneymergebe.domain.record.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssetType {
    CARD("카드"),
    CASH("현금"),
    ACCOUNT("계좌");
    private final String value;
}
