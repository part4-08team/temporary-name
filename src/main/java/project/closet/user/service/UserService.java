package project.closet.user.service;

import project.closet.dto.request.UserCreateRequest;
import project.closet.dto.response.UserDto;

public interface UserService {

    // 회원가입 로직
    UserDto create(UserCreateRequest userCreateRequest);
}
