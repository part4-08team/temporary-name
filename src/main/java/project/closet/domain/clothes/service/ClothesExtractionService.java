package project.closet.domain.clothes.service;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import project.closet.domain.clothes.dto.response.ClothesDto;
import project.closet.exception.clothes.ExtractionException;
import project.closet.exception.clothes.UnsupportedShopException;

@Service
public class ClothesExtractionService {

    public ClothesDto extractFromUrl(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                            + "AppleWebKit/537.36 (KHTML, like Gecko) "
                            + "Chrome/115.0.0.0 Safari/537.36")
                    .timeout(5000)
                    .get();

            Element titleMeta = doc.selectFirst("meta[property=og:title]");
            Element imgMeta   = doc.selectFirst("meta[property=og:image]");

            String name = titleMeta != null ? titleMeta.attr("content") : null;
            String imageUrl = imgMeta   != null ? imgMeta.attr("content")   : null;

            return ClothesDto.extraction(name, imageUrl);

        } catch (IOException e) {
            throw new UnsupportedShopException(url);
        }
    }
}
