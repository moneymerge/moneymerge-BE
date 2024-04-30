package example.com.moneymergebe.domain.character.repository;

import example.com.moneymergebe.domain.character.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacterRepository extends JpaRepository<Character, Long> {
    Character findByCharacterId(Long characterId);

}
