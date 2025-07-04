package project.closet.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.closet.domain.base.BaseUpdatableEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "profiles")
public class Profile extends BaseUpdatableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 50)
    private Gender gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "profile_image_url", length = 1024)
    private String profileImageUrl;

    @Column(name = "temperature_sensitivity", length = 50)
    private Integer temperatureSensitivity;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "location_name", length = 50)
    private String locationName;

    @Builder
    public Profile(User user,
            Gender gender,
            LocalDate birthDate,
            String profileImageUrl,
            Integer temperatureSensitivity,
            Double latitude,
            Double longitude,
            String locationName) {
        this.user = user;
        this.gender = gender;
        this.birthDate = birthDate;
        this.profileImageUrl = profileImageUrl;
        this.temperatureSensitivity = temperatureSensitivity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
    }

    public static Profile createDefault(User user) {
        Profile profile = Profile.builder().user(user).build();
        user.setProfileInternal(profile);
        return profile;
    }

    public void updateProfile(
            Gender newGender,
            LocalDate newBirthDate,
            String newProfileImageUrl,
            Integer newTemperatureSensitivity,
            Double newLatitude,
            Double newLongitude,
            String newLocationName
    ) {
        if (newGender != null && !newGender.equals(this.gender)) {
            this.gender = newGender;
        }
        if (newBirthDate != null && !newBirthDate.equals(this.birthDate)) {
            this.birthDate = newBirthDate;
        }
        if (newProfileImageUrl != null && !newProfileImageUrl.equals(this.profileImageUrl)) {
            this.profileImageUrl = newProfileImageUrl;
        }
        if (newTemperatureSensitivity != null && !newTemperatureSensitivity.equals(
                this.temperatureSensitivity)) {
            this.temperatureSensitivity = newTemperatureSensitivity;
        }
        if (newLatitude != null && !newLatitude.equals(this.latitude)) {
            this.latitude = newLatitude;
        }
        if (newLongitude != null && !newLongitude.equals(this.longitude)) {
            this.longitude = newLongitude;
        }
        if (newLocationName != null && !newLocationName.equals(this.locationName)) {
            this.locationName = newLocationName;
        }
    }

}
