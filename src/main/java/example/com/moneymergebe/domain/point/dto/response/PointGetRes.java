package example.com.moneymergebe.domain.point.dto.response;

import example.com.moneymergebe.domain.point.entity.Point;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class PointGetRes {
    private Long pointId;
    private LocalDateTime createdAt;
    private String detail;
    private Long point;

    public PointGetRes(Point point) {
        this.pointId = point.getPointId();
        this.createdAt = point.getCreatedAt();
        this.detail = point.getDetail();
        this.point = point.getPoint();
    }
}
