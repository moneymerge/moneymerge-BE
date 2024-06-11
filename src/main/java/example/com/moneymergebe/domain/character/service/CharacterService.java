package example.com.moneymergebe.domain.character.service;

import example.com.moneymergebe.domain.character.dto.request.CharacterBuyReq;
import example.com.moneymergebe.domain.character.dto.request.CharacterChangeReq;
import example.com.moneymergebe.domain.character.dto.response.CharacterBuyRes;
import example.com.moneymergebe.domain.character.dto.response.CharacterChangeRes;
import example.com.moneymergebe.domain.character.dto.response.CharacterGetRes;
import example.com.moneymergebe.domain.character.dto.response.CharacterShopGetRes;
import example.com.moneymergebe.domain.character.dto.response.OwnCharacterRes;
import example.com.moneymergebe.domain.character.entity.Character;
import example.com.moneymergebe.domain.character.repository.CharacterRepository;
import example.com.moneymergebe.domain.point.entity.Point;
import example.com.moneymergebe.domain.point.repository.PointRepository;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.entity.UserCharacter;
import example.com.moneymergebe.domain.user.repository.UserCharacterRepository;
import example.com.moneymergebe.domain.user.repository.UserRepository;
import example.com.moneymergebe.global.exception.GlobalException;
import example.com.moneymergebe.global.validator.CharacterValidator;
import example.com.moneymergebe.global.validator.UserCharacterValidator;
import example.com.moneymergebe.global.validator.UserValidator;
import java.util.List;
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
    private final PointRepository pointRepository;
    private final static String PURCHASE_CHARACTER = "캐릭터 구매";

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
     * 소유 캐릭터 ID 리스트 조회
     */
    @Transactional(readOnly = true)
    public OwnCharacterRes getUserOwnCharacters(Long userId) {
        User user = findUser(userId);
        List<UserCharacter> characters = userCharacterRepository.findAllByUser(user);
        Long[] characterList = new Long[characters.size()];
        for (int i = 0; i < characters.size(); i++) {
            characterList[i] = characters.get(i).getCharacter().getCharacterId();
        }

        return new OwnCharacterRes(characterList);
    }

    /**
     * 캐릭터 변경
     */
    @Transactional
    public CharacterChangeRes changeCharacter(Long userId, CharacterChangeReq req) {
        User user = findUser(userId);
        Character character = findCharacter(req.getCharacterId());
        UserCharacterValidator.checkCharacterToChange(hasCharacter(user, character)); // 사용자가 가지고 있는 character가 아니면 예외 발생
        user.updateCharacter(character.getCharacterId());
        return new CharacterChangeRes();
    }

    /**
     * 모든(상점) 캐릭터 조회
     */
    @Transactional(readOnly = true)
    public Page<CharacterShopGetRes> getShopCharacters(Long userId, int page, int size, String sortBy, boolean isAsc) {
        User user = findUser(userId);
        Sort.Direction direction = isAsc ? Direction.ASC : Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return characterRepository.findAll(pageable).map(
            character -> new CharacterShopGetRes(character, hasCharacter(user, character))
        );
    }

    /**
     * 캐릭터 구매
     */
    @Transactional
    public CharacterBuyRes buyCharacter(Long userId, CharacterBuyReq req) {
        User user = findUser(userId);
        Character character = findCharacter(req.getCharacterId());

        UserValidator.checkUserPoints(user, character.getPoints()); // 포인트가 충분한지 검사
        UserCharacterValidator.checkCharacterToBuy(hasCharacter(user, character)); // 이미 가지고 있는지 검사

        UserCharacter userCharacter = UserCharacter.builder().user(user).character(character).build();
        userCharacterRepository.save(userCharacter);
        user.updatePoints(-1*character.getPoints());

        pointRepository.save(Point.builder().detail(PURCHASE_CHARACTER).points(-1*character.getPoints()).user(user).build());

        return new CharacterBuyRes();
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
     * UserCharacter 존재 유무 반환
     */
    private boolean hasCharacter(User user, Character character) {
        UserCharacter userCharacter = userCharacterRepository.findByUserAndCharacter(user, character);
        return userCharacter != null;
    }
}
