package project.closet.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import project.closet.dto.response.UserDto;

/*
    스프링 시큐리티에서 스프링 컨텍스트에서 핸들링하는 유저 정보를
    저장하고 있는 객체입니다.
 */
@RequiredArgsConstructor
public class ClosetUserDetails implements UserDetails {

    @Getter
    private final UserDto userDto;
    private final String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_".concat(userDto.role().name())));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userDto.username();
    }

    public UUID getUserId() {
        return userDto.id();
    }
}
