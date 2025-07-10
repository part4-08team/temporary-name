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

    public static ClosetUserDetails from(User user) {
        return new ClosetUserDetails(
                user.getId(),
                user.getRole(),
                user.getName(),
                user.getEmail(),
                user.getPassword()
        );
    }

    public static ClosetUserDetails from(JwtObject jwtObject) {
        return new ClosetUserDetails(
                jwtObject.userId(),
                jwtObject.role(),
                jwtObject.name(),
                jwtObject.email(),
                null // JWT does not contain password
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

}
