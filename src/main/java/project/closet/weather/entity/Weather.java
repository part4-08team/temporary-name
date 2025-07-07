package project.closet.weather.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.closet.domain.base.BaseEntity;

@Getter
@Entity
@Table(name = "weathers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Weather extends BaseEntity {

    @Column(nullable = false)
    private Instant forecastedAt; // 예보 생성 시각

    @Column(nullable = false)
    private Instant forecastAt; // 예보 대상 시각

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SkyStatus skyStatus;

    @Column(nullable = false)
    private Double amount;  // 강수량

    @Column(nullable = false)
    private Integer probability; // 강수 확률

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PrecipitationType precipitationType; // 강수 형태

    @Column(nullable = false)
    private Double windSpeed; // 풍속

    @Column(nullable = false, length = 50)
    private AsWord asWord; // 풍속 상태를 설명하는 단어

    @Column(nullable = false)
    private Double humidity; // 습도

    @Column(nullable = false)
    private Double currentTemperature; // 현재 기온

    @Column(nullable = false)
    private Double maxTemperature; // 최고 기온

    @Column(nullable = false)
    private Double minTemperature; // 최저 기온

    @Column(nullable = false)
    private Integer x;

    @Column(nullable = false)
    private Integer y;

    @Builder
    public Weather(Instant forecastedAt, Instant forecastAt, SkyStatus skyStatus, Double amount,
            Integer probability, PrecipitationType precipitationType, Double windSpeed, AsWord asWord,
            Double humidity, Double currentTemperature, Double maxTemperature,
            Double minTemperature,
            Integer x, Integer y) {
        this.forecastedAt = forecastedAt;
        this.forecastAt = forecastAt;
        this.skyStatus = skyStatus;
        this.amount = amount;
        this.probability = probability;
        this.precipitationType = precipitationType;
        this.windSpeed = windSpeed;
        this.asWord = asWord;
        this.humidity = humidity;
        this.currentTemperature = currentTemperature;
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
        this.x = x;
        this.y = y;
    }
}
