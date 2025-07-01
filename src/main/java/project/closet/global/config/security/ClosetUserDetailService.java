package project.closet.global.config.security;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.closet.domain.users.User;
import project.closet.domain.users.repository.UserRepository;


@Service
@Primary
@RequiredArgsConstructor
public class ClosetUserDetailService implements UserDetailsService {

  private final UserRepository userRepository;
  private final JwtBlackList jwtBlackList;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("Wrong email : No User matches"));

    if (user.isLocked()) {
      throw new BadCredentialsException("Your account is locked.");
    }

    String role = determineRole(user);
    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

    return new ClosetUserDetails(
        user.getId(),
        user.getEmail(),
        user.getPassword(),
        authorities
    );
  }


  /**
   * 임시 비밀번호 상태 : 어쩄든 로그인 시켜야 함 (temp prefix)
   * 임시 비밀번호 + 시간 타임아웃 : 비밀번호 변경 못하게 throws
   * 임시 비밀버놓 + 시간 타임 인 : 비밀번호 변경하게 TEMP role부여
   */
  // user의 필드가 거의 변할 일이 없다는 가정 하에서 redis가 아닌 updatedAt를 활용  (locked정도 변할 수 있는데 그정도는..)
  private String determineRole(User user) {
    if (!user.isTemporaryPassword()) {
      return String.valueOf(user.getRole());
    }

    if (isTemporaryPwdExpired(user.getUpdatedAt())) {
      throw new UsernameNotFoundException("타임 아웃 : 임시 비밀번호로 로그인할 수 없습니다.");
    }
    return "TEMP";
  }

  /**
   * false : 10분 이하
   * true : 10분 이상
   */
  private boolean isTemporaryPwdExpired(Instant updatedAt) {
    Duration afterIssueTemporaryPwd = Duration.between(updatedAt, Instant.now());
    return afterIssueTemporaryPwd.minusMinutes(10).isNegative();
  }
}
