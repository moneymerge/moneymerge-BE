package example.com.moneymergebe.domain.receipt.repository;

import example.com.moneymergebe.domain.receipt.entity.Receipt;
import example.com.moneymergebe.domain.receipt.entity.ReceiptLike;
import example.com.moneymergebe.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptLikeRepository extends JpaRepository<ReceiptLike, Long> {

    List<ReceiptLike> findAllByUser(User user);

    ReceiptLike findByUserAndReceipt(User user, Receipt receipt);

    int countByReceipt(Receipt receipt);
}
