package example.com.moneymergebe.domain.character.service;

import example.com.moneymergebe.domain.character.dto.response.CharacterChangeRes;
import example.com.moneymergebe.domain.character.dto.response.CharacterGetRes;
import example.com.moneymergebe.domain.character.entity.Character;
import example.com.moneymergebe.domain.character.repository.CharacterRepository;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.entity.UserCharacter;
import example.com.moneymergebe.domain.user.repository.UserCharacterRepository;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.exception.GlobalException;
import example.com.moneymergebe.global.validator.CharacterValidator;
import example.com.moneymergebe.global.validator.UserCharacterValidator;
import example.com.moneymergebe.global.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CharacterService {
    private final UserCharacterRepository userCharacterRepository;
    private final CharacterRepository characterRepository;
    private final UserRepository userRepository;

    /**
     * 소유 캐릭터 조회
     */
    @Transactional(readOnly = true)
    public Page<CharacterGetRes> getUserCharacters(Long userId, int page, int size, String sortBy, boolean isAsc) {
        User user = findUser(userId);
        Sort.Direction direction = isAsc ? Direction.ASC : Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return userCharacterRepository.findAllByUser(user, pageable).map(CharacterGetRes::new);
    }

    /**
     * 캐릭터 변경
     */
    @Transactional
    public CharacterChangeRes changeCharacter(Long userId, Long characterId) {
        User user = findUser(userId);
        Character character = findCharacter(characterId);
        findUserCharacter(user, character); // 사용자가 가지고 있는 character가 아니면 예외 발생
        user.updateCharacter(characterId);
        return new CharacterChangeRes();
    }

    /**
     * @throws GlobalException userId에 해당하는 사용자가 존재하지 않는 경우 예외 발생
     */
    private User findUser(Long userId) {
        User user = userRepository.findByUserId(userId);
        UserValidator.validate(user);
        return user;
    }

    /**
     * @throws GlobalException characterId 해당하는 캐릭터가 존재하지 않는 경우 예외 발생
     */
    private Character findCharacter(Long characterId) {
        Character character = characterRepository.findByCharacterId(characterId);
        CharacterValidator.validate(character);
        return character;
    }

    /**
     * @throws GlobalException user가 character를 소유하지 않을 경우 예외 발생
     */
    private UserCharacter findUserCharacter(User user, Character character) {
        UserCharacter userCharacter = userCharacterRepository.findByUserAndCharacter(user, character);
        UserCharacterValidator.validate(userCharacter);
        return userCharacter;
    }
}
