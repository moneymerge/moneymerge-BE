package example.com.moneymergebe.domain.character.entity;

import example.com.moneymergebe.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_character")
public class Character extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long characterId;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    String image;

    @Column(nullable = false)
    int points;

    @Builder
    private Character(String name, String image, int points) {
        this.name = name;
        this.image = image;
        this.points = points;
    }
}
