package project.closet.domain.recommend;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import project.closet.domain.clothes.entity.ClothesType;

class TemperatureCategoryTest {

    @ParameterizedTest
    @CsvSource({
            "28.0, VERY_HOT",
            "30.5, VERY_HOT",
            "23.0, HOT",
            "25.0, HOT",
            "20.0, WARM",
            "22.9, WARM",
            "17.0, COOL",
            "19.9, COOL",
            "12.0, CHILLY",
            "16.9, CHILLY",
            "9.0, COLD",
            "11.9, COLD",
            "5.0, VERY_COLD",
            "8.9, VERY_COLD",
            "0.0, FREEZING",
            "4.9, FREEZING"
    })
    @DisplayName("of(temp)가 올바른 카테고리를 반환해야 한다")
    void of_returnsCorrectCategory(double temp, TemperatureCategory expected) {
        assertThat(TemperatureCategory.of(temp)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            // inRange 포함/제외 테스트: min은 포함, max는 제외
            "VERY_HOT, 28.0, true",
            "VERY_HOT, 1000.0, true",
            "VERY_HOT, 27.999, false",
            "HOT, 23.0, true",
            "HOT, 22.999, false",
            "HOT, 27.999, true",
            "HOT, 28.0, false"
    })
    @DisplayName("inRange(temp)가 구간 경계를 올바르게 처리해야 한다")
    void inRange_handlesBoundsCorrectly(String enumName, double temp, boolean expected) {
        TemperatureCategory cat = TemperatureCategory.valueOf(enumName);
        assertThat(cat.inRange(temp)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "WARM, TOP, 반팔;긴팔",
            "WARM, OUTER, 카디건;후드 집업",
            "FREEZING, SCARF, 목도리",
            "FREEZING, HAT, 비니;트루퍼"
    })
    @DisplayName("allowedDetails(type)이 매핑된 상세속성 집합을 반환해야 한다")
    void allowedDetails_returnsCorrectDetails(TemperatureCategory cat,
            ClothesType type, String expectedDetailsCsv) {

        Set<String> actual = cat.allowedDetails(type);
        Set<String> expected = Set.of(expectedDetailsCsv.split(";"));
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "WARM, TOP;BOTTOM;OUTER;SHOES",
            "HOT, TOP;BOTTOM;SHOES",
            "FREEZING, TOP;BOTTOM;OUTER;SCARF;ACCESSORY;HAT;SHOES"
    })
    @DisplayName("allowedTypes()가 매핑된 대분류 키 셋을 반환해야 한다")
    void allowedTypes_returnsAllMappedTypes(TemperatureCategory cat, String expectedTypesCsv) {
        Set<ClothesType> actual = Set.copyOf(cat.allowedTypes());
        Set<ClothesType> expected = Set.of(expectedTypesCsv.split(";")).stream()
                .map(ClothesType::valueOf).collect(toSet());
        assertThat(actual).isEqualTo(expected);
    }
}
