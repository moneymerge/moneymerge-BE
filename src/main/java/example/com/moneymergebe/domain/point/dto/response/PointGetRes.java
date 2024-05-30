package example.com.moneymergebe.domain.point.dto.response;

import example.com.moneymergebe.domain.point.entity.Point;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;

@Getter
public class PointGetRes {
    private Long pointId;
    private String createdAt;
    private String detail;
    private int points;

    public PointGetRes(Point point) {
        this.pointId = point.getPointId();
        this.createdAt = point.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.detail = point.getDetail();
        this.points = point.getPoints();
    }
}
