package project.closet.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import java.time.LocalDate;
import project.closet.domain.base.BaseUpdatableEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "profiles")
public class Profile extends BaseUpdatableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "gender", length = 50)
    private String gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "profile_image_url", length = 1024)
    private String profileImageUrl;

    @Column(name = "temperature_sensitivity", length = 50)
    private String temperatureSensitivity;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "location_name", length = 50)
    private String locationName;

    public Profile(User user,
            String gender,
            LocalDate birthDate,
            String profileImageUrl,
            String temperatureSensitivity,
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

    public void updateProfile(String newGender,
            LocalDate newBirthDate,
            String newProfileImageUrl,
            String newTemperatureSensitivity,
            Double newLatitude,
            Double newLongitude,
            String newLocationName) {
        if (newGender != null && !newGender.equals(this.gender)) {
            this.gender = newGender;
        }
        if (newBirthDate != null && !newBirthDate.equals(this.birthDate)) {
            this.birthDate = newBirthDate;
        }
        if (newProfileImageUrl != null && !newProfileImageUrl.equals(this.profileImageUrl)) {
            this.profileImageUrl = newProfileImageUrl;
        }
        if (newTemperatureSensitivity != null && !newTemperatureSensitivity.equals(this.temperatureSensitivity)) {
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

