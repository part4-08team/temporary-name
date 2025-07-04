package project.closet.user.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.closet.domain.base.BaseUpdatableEntity;
import project.closet.dto.request.ProfileUpdateRequest;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

    @Column(name = "name", nullable = false, length = 50, unique = true)
    private String name;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "locked", nullable = false, columnDefinition = "boolean default false")
    private boolean locked;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(name = "is_temporary_password", nullable = false, columnDefinition = "boolean default false")
    private boolean isTemporaryPassword;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void updateRole(Role newRole) {
        if (this.role != newRole) {
            this.role = newRole;
        }
    }

    // Profile 양방향 연관관계 설정을 위한 메서드
    public void setProfileInternal(Profile profile) {
        this.profile = profile;
    }

    public void updateProfile(ProfileUpdateRequest request) {
        if (request.name() != null && !name.equals(request.name())) {
            this.name = request.name();
        }
        this.profile.updateProfile(
                request.gender(),
                request.birthDate(),
                null,
                request.temperatureSensitivity(),
                request.location().latitude(),
                request.location().longitude(),
                null
        );
    }
}
