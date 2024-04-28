package example.com.moneymergebe.domain.receipt.repository;

import example.com.moneymergebe.domain.receipt.entity.Receipt;
import example.com.moneymergebe.domain.receipt.entity.ReceiptLike;
import example.com.moneymergebe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptLikeRepository extends JpaRepository<ReceiptLike, Long> {

    void deleteAllByReceipt(Receipt receipt);

    ReceiptLike findByUserAndReceipt(User user, Receipt receipt);
}
