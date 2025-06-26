package project.closet.global.config.security;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.closet.domain.users.user.User;
import project.closet.domain.users.user.UserRepository;
import project.closet.domain.users.user.UserRole;

@RequiredArgsConstructor
@Service
public class ClosetUserDetailService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다."));

    return new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        user.getPassword(),
        getAuthorities(user.getRole())
    );
  }

  private List<SimpleGrantedAuthority> getAuthorities(UserRole role) {
    return List.of(new SimpleGrantedAuthority(String.valueOf(role)));
  }
}
