package project.closet.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class KakaoAddressResponse {
    private Meta meta;
    private List<Document> documents;

    @Getter
    @NoArgsConstructor
    public static class Meta {
        private int total_count;
    }

    @Getter
    @NoArgsConstructor
    public static class Document {
        private String region_type;
        private String code;
        private String address_name;
        private String region_1depth_name;
        private String region_2depth_name;
        private String region_3depth_name;
        private String region_4depth_name;
        private double x;
        private double y;
    }
}
