package project.closet.user.mapper;

import org.mapstruct.Mapper;
import project.closet.dto.response.UserDto;
import project.closet.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);
}
