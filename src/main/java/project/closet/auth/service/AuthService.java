package project.closet.auth.service;

import project.closet.dto.request.RoleUpdateRequest;
import project.closet.dto.response.UserDto;

public interface AuthService {

    void initAdmin();

    UserDto updateRole(RoleUpdateRequest request);
}
