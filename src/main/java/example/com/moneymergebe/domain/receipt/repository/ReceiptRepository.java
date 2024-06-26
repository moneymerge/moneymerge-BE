package example.com.moneymergebe.domain.receipt.repository;

import example.com.moneymergebe.domain.receipt.entity.Receipt;
import example.com.moneymergebe.domain.user.entity.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Receipt findByReceiptId(Long receiptId);

    Receipt findByDateAndUser(LocalDate date, User user);

    List<Receipt> findAllByUserAndDateBetweenOrderByDate(User user, LocalDate startDate, LocalDate endDate);

    List<Receipt> findBySharedTrueAndUserNot(User user);

    List<Receipt> findAllBySharedTrueAndUserNot(User user);

    List<Receipt> findAllByUser(User user);
}
