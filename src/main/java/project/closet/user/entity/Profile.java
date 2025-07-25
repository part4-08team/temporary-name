package project.closet.user.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
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

    @Column(name = "profile_image_key", length = 1024)
    private String profileImageKey;

    @Column(name = "temperature_sensitivity", length = 50)
    private Integer temperatureSensitivity;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @ElementCollection
    @CollectionTable(
            name = "profile_location_names",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Column(name = "location_name", length = 50)
    @BatchSize(size = 10)
    private List<String> locationNames = new ArrayList<>();

    @Builder
    public Profile(User user,
            Gender gender,
            LocalDate birthDate,
            String profileImageKey,
            Integer temperatureSensitivity,
            Double latitude,
            Double longitude,
            List<String> locationName) {
        this.user = user;
        this.gender = gender;
        this.birthDate = birthDate;
        this.profileImageKey = profileImageKey;
        this.temperatureSensitivity = temperatureSensitivity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationNames = locationName;
    }

    public static Profile createDefault(User user) {
        Profile profile = Profile.builder()
                .user(user)
                .build();
        user.setProfileInternal(profile);
        return profile;
    }

    public void updateGender(Gender newGender) {
        if (newGender != null && !newGender.equals(this.gender)) {
            this.gender = newGender;
        }
    }

    public void updateBirthDate(LocalDate newBirthDate) {
        if (newBirthDate != null && !newBirthDate.equals(this.birthDate)) {
            this.birthDate = newBirthDate;
        }
    }

    public void updateTemperatureSensitivity(Integer newTemperatureSensitivity) {
        if (newTemperatureSensitivity != null
                && !newTemperatureSensitivity.equals(this.temperatureSensitivity)) {
            this.temperatureSensitivity = newTemperatureSensitivity;
        }
    }

    public void updateLocation(Double newLatitude, Double newLongitude, List<String> newLocationName) {
        if (newLatitude != null && !newLatitude.equals(this.latitude)) {
            this.latitude = newLatitude;
        }
        if (newLongitude != null && !newLongitude.equals(this.longitude)) {
            this.longitude = newLongitude;
        }
        if (newLocationName != null && !newLocationName.equals(this.locationNames)) {
            this.locationNames = new ArrayList<>(newLocationName);
        }
    }

    public void updateProfile(
            Gender newGender,
            LocalDate newBirthDate,
            Integer newTemperatureSensitivity,
            Double newLatitude,
            Double newLongitude,
            List<String> newLocationName
    ) {
        if (newGender != null && !newGender.equals(this.gender)) {
            this.gender = newGender;
        }
        if (newBirthDate != null && !newBirthDate.equals(this.birthDate)) {
            this.birthDate = newBirthDate;
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
        if (newLocationName != null && !newLocationName.equals(this.locationNames)) {
            this.locationNames = new ArrayList<>(newLocationName);
        }
    }

    public void updateProfileImageKey(String profileImageKey) {
        if (profileImageKey != null && !profileImageKey.equals(this.profileImageKey)) {
            this.profileImageKey = profileImageKey;
        }
    }

}
