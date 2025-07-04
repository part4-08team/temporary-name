package project.closet.user.service;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import project.closet.dto.request.ProfileUpdateRequest;
import project.closet.dto.request.UserCreateRequest;
import project.closet.dto.response.ProfileDto;
import project.closet.dto.response.UserDto;

public interface UserService {

    // 회원 가입
    UserDto create(UserCreateRequest userCreateRequest);

    // 회원 조회
    ProfileDto getProfile(UUID userId);

    // 회원 정보 수정
    ProfileDto updateProfile(UUID userId, ProfileUpdateRequest profileUpdateRequest,
            MultipartFile profileImage);
}
