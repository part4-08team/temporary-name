package project.closet.domain.recommend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.stream.Collectors;
import project.closet.domain.clothes.entity.ClothesType;

public enum TemperatureCategory {
    VERY_HOT(28, Double.MAX_VALUE, Map.of(
            ClothesType.TOP,    Set.of("반팔"),
            ClothesType.BOTTOM, Set.of("반바지"),
            ClothesType.SHOES,  Set.of("슬리퍼/샌들", "스포츠화")
    )),
    HOT(23, 28, Map.of(
            ClothesType.TOP,    Set.of("반팔"),
            ClothesType.BOTTOM, Set.of("반바지"),
            ClothesType.SHOES,  Set.of("스니커즈", "슬리퍼/샌들", "스포츠화")
    )),
    WARM(20, 23, Map.of(
            ClothesType.TOP,    Set.of("반팔", "긴팔"),
            ClothesType.BOTTOM, Set.of("반바지", "긴바지"),
            ClothesType.OUTER,  Set.of("카디건", "후드 집업"),
            ClothesType.SHOES,  Set.of("스니커즈","구두")
    )),
    COOL(17, 20, Map.of(
            ClothesType.TOP,    Set.of("긴팔"),
            ClothesType.BOTTOM, Set.of("긴바지"),
            ClothesType.OUTER,  Set.of("카디건", "트러커 재킷", "후드 집업"),
            ClothesType.SHOES,  Set.of("스니커즈", "부츠/워커","구두")
    )),
    CHILLY(12, 17, Map.of(
            ClothesType.TOP,    Set.of("긴팔"),
            ClothesType.BOTTOM, Set.of("긴바지"),
            ClothesType.OUTER,  Set.of("트러커 재킷", "코트"),
            ClothesType.SHOES,  Set.of("부츠/워커")
    )),
    COLD(9, 12, Map.of(
            ClothesType.TOP,    Set.of("긴팔"),
            ClothesType.BOTTOM, Set.of("긴바지"),
            ClothesType.OUTER,  Set.of("코트", "패딩"),
            ClothesType.SHOES,  Set.of("부츠/워커")
    )),
    VERY_COLD(5, 9, Map.of(
            ClothesType.TOP,    Set.of("긴팔"),
            ClothesType.BOTTOM, Set.of("긴바지"),
            ClothesType.OUTER,  Set.of("패딩"),
            ClothesType.SHOES,  Set.of("부츠/워커")
    )),
    FREEZING(Double.MIN_VALUE, 5, Map.of(
            ClothesType.TOP,        Set.of("긴팔"),
            ClothesType.BOTTOM,     Set.of("긴바지"),
            ClothesType.OUTER,      Set.of("패딩"),
            ClothesType.SCARF,      Set.of("목도리"),
            ClothesType.ACCESSORY,  Set.of("장갑"),
            ClothesType.HAT,        Set.of("비니", "트루퍼"),
            ClothesType.SHOES,      Set.of("부츠/워커","패딩/퍼 신발")
    ));

    private final double minTemp;
    private final double maxTemp;
    private final Map<ClothesType, Set<String>> allowedDetailValues;

    TemperatureCategory(double minTemp,
            double maxTemp,
            Map<ClothesType, Set<String>> allowedDetailValues) {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.allowedDetailValues = allowedDetailValues;
    }

    /** 이 구간에 temp가 속하는지 확인 (minTemp ≤ temp < maxTemp) */
    public boolean inRange(double temp) {
        return temp >= minTemp && temp < maxTemp;
    }

    /** 이 구간에서 허용할 대분류(ClothesType) 목록 */
    public List<ClothesType> allowedTypes() {
        // allowedDetailValues에 키로 정의된 ClothesType 전체 반환
        return new ArrayList<>(allowedDetailValues.keySet());
    }

    /** 대분류별로 허용할 세부 속성(의상 상세 종류) 리스트를 꺼냅니다. */
    public Set<String> allowedDetails(ClothesType type) {
        return allowedDetailValues.getOrDefault(type, Set.of());
    }

    /** temp에 맞는 카테고리 상수를 찾아 반환합니다. */
    public static TemperatureCategory of(double temp) {
        return Arrays.stream(values())
                .filter(cat -> cat.inRange(temp))
                .findFirst()
                .orElse(VERY_HOT);
    }
}
