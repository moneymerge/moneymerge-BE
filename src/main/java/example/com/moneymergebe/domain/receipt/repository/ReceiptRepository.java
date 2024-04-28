package example.com.moneymergebe.domain.receipt.repository;

import example.com.moneymergebe.domain.receipt.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

}
