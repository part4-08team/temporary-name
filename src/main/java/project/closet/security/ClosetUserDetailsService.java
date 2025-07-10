package project.closet.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.exception.user.UserNotFoundException;
import project.closet.user.entity.User;
import project.closet.user.repository.UserRepository;

/*
    DaoAuthenticationProvider
    에서 유저의 정보를 인증하는 서비스입니다.
 */

@Service
@RequiredArgsConstructor
public class ClosetUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.withEmail(email));
        return ClosetUserDetails.from(user);
    }
}
