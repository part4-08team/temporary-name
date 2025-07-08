package project.closet.weather.location;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherLocationInitializer implements ApplicationRunner {

    private final WeatherLocationRepository locationRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (locationRepository.count() > 0) {
            log.info("📍 weather_locations already initialized. Skipping...");
            return;
        }

        ZipSecureFile.setMinInflateRatio(0.0001); // zip bomb 방지 완화

        try (InputStream is = new ClassPathResource("weather_locations.xlsx").getInputStream();
                Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Set<String> uniqueKeys = new HashSet<>();
            List<WeatherLocation> locations = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // 헤더 스킵
                }

                int x = getIntValue(row, 5);
                int y = getIntValue(row, 6);

                String key = x + "," + y;
                if (uniqueKeys.add(key)) {
                    locations.add(new WeatherLocation(x, y));
                }
            }

            locationRepository.saveAll(locations);
            log.info("✅ {}개의 좌표가 weather_locations 테이블에 저장되었습니다.", locations.size());

        } catch (Exception e) {
            log.error("❌ 엑셀 파싱 중 오류 발생", e);
        }
    }

    private int getIntValue(Row row, int colIdx) {
        var cell = row.getCell(colIdx);
        return switch (cell.getCellType()) {
            case NUMERIC -> (int) cell.getNumericCellValue();
            case STRING -> Integer.parseInt(cell.getStringCellValue().trim());
            default -> throw new IllegalStateException("지원하지 않는 셀 타입: " + cell.getCellType());
        };
    }
}
