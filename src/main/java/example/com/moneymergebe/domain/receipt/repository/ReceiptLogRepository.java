package example.com.moneymergebe.domain.receipt.repository;

import example.com.moneymergebe.domain.receipt.entity.Receipt;
import example.com.moneymergebe.domain.receipt.entity.ReceiptLog;
import example.com.moneymergebe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptLogRepository extends JpaRepository<ReceiptLog, Long> {
    boolean existsByUserAndReceipt(User user, Receipt receipt);
}
