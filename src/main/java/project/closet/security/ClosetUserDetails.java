package project.closet.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import project.closet.security.jwt.JwtObject;
import project.closet.user.entity.Role;
import project.closet.user.entity.User;

/*
    스프링 시큐리티에서 스프링 컨텍스트에서 핸들링하는 유저 정보를
    저장하고 있는 객체입니다.
 */
@Getter
@RequiredArgsConstructor
public class ClosetUserDetails implements UserDetails {

    private final UUID userId;
    private final Role role;
    private final String name;
    private final String email;
    private final String password;
    private final boolean locked;

    public static ClosetUserDetails from(User user) {
        return new ClosetUserDetails(
                user.getId(),
                user.getRole(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.isLocked()
        );
    }

    public static ClosetUserDetails from(JwtObject jwtObject) {
        return new ClosetUserDetails(
                jwtObject.userId(),
                jwtObject.role(),
                jwtObject.name(),
                jwtObject.email(),
                null, // JWT does not contain password,
                true // JWT does not have locked status, assuming non-locked
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_".concat(role.name())));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    /**
     * 계정이 잠금되지 않았는지 확인합니다.
     * @return
     * true 를 반환하면 계정이 잠기지 않았음을 의미합니다.
     */
    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }
}
