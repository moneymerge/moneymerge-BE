package example.com.moneymergebe.domain.point.repository;

import example.com.moneymergebe.domain.point.entity.Point;
import example.com.moneymergebe.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Long> {
    Page<Point> findAllByUser(User user, Pageable pageable);
}
