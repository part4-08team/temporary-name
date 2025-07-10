package project.closet.user.service;

import java.util.UUID;
import org.hibernate.query.SortDirection;
import org.springframework.web.multipart.MultipartFile;
import project.closet.dto.request.ProfileUpdateRequest;
import project.closet.dto.request.UserCreateRequest;
import project.closet.dto.request.UserLockUpdateRequest;
import project.closet.dto.request.UserRoleUpdateRequest;
import project.closet.dto.response.ProfileDto;
import project.closet.dto.response.UserDto;
import project.closet.dto.response.UserDtoCursorResponse;
import project.closet.user.entity.Role;

public interface UserService {

    // 회원 가입
    UserDto create(UserCreateRequest userCreateRequest);

    // 회원 조회
    ProfileDto getProfile(UUID userId);

    // 회원 정보 수정
    ProfileDto updateProfile(UUID userId, ProfileUpdateRequest profileUpdateRequest,
            MultipartFile profileImage);

    // 권한 수정
    UserDto updateRole(UUID userid, UserRoleUpdateRequest userRoleUpdateRequest);

    // 회원 잠금 상태 수정
    UUID updateLockStatus(UUID userId, UserLockUpdateRequest userRoleUpdateRequest);

    // 회원 조회
    UserDtoCursorResponse findAll(
            String cursor,
            UUID idAfter,
            int limit,
            String sortBy,
            SortDirection sortDirection,
            String emailLike,
            Role roleEqual,
            Boolean locked
    );
}
