package example.com.meneymergebe.global.security;

import example.com.meneymergebe.domain.user.entity.User;
import example.com.meneymergebe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(
            () -> new UsernameNotFoundException(userId)
            // TODO: 예외 처리 설정 이후 공통 예외로 수정
        );

        return new UserDetailsImpl(user);
    }
}
