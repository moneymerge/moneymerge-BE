package example.com.moneymergebe.domain.receipt.dto.request;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReceiptModifyReq {
    private LocalDate date;
    private String content;
    private int positive;
    private int negative;
    private boolean shared;
    private Long userId;
    private Long receiptId;
}
