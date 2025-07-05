package vacancy.fetch.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import vacancy.fetch.VacancyIdFetchingService;
import vacancy.fetch.dto.VacancySearchRequestDto;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация VacancyIdFetchingService для hh.ru.
 * Получает идентификаторы вакансий по поисковому запросу.
 */
@Slf4j
public class HhVacancyIdFetchingService implements VacancyIdFetchingService {

    private static final String BASE_URL = "https://api.hh.ru/vacancies";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public List<String> fetchVacancyIds(VacancySearchRequestDto req) throws Exception {
        List<String> ids = new ArrayList<>();
        int page = 0;
        int perPage = Math.min(req.getPerPage(), 100); // ограничение API
        int maxPages = req.getMaxPages();

        while (page < maxPages) {
            String url = buildUrl(req, page, perPage);
            JsonNode response = sendGet(url);

            JsonNode items = response.get("items");
            if (items == null || !items.isArray() || items.isEmpty()) break;

            for (JsonNode item : items) {
                String id = item.path("id").asText();
                if (!id.isEmpty()) {
                    ids.add(id);
                }
            }
            int totalPages = response.get("pages").asInt();
            page++;
            if (page >= totalPages) break;
            Thread.sleep(400 + (int) (Math.random() * 300)); // Анти-бан, не ddosим hh.ru
        }
        log.info("Получено идентификаторов вакансий: {}", ids.size());
        return ids;
    }

    private String buildUrl(VacancySearchRequestDto req, int page, int perPage) {
        StringBuilder url = new StringBuilder(BASE_URL)
                .append("?text=").append(URLEncoder.encode(req.getQuery(), StandardCharsets.UTF_8))
                .append("&per_page=").append(perPage)
                .append("&page=").append(page);

        if (req.getArea() != null)
            url.append("&area=").append(req.getArea());
        if (req.getDateFrom() != null)
            url.append("&date_from=").append(req.getDateFrom().format(DateTimeFormatter.ISO_LOCAL_DATE));
        if (req.getDateTo() != null)
            url.append("&date_to=").append(req.getDateTo().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return url.toString();
    }

    private JsonNode sendGet(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "VacancyIdFetcherBot/1.0 (for study)");
        try (InputStream is = conn.getInputStream()) {
            return MAPPER.readTree(is);
        }
    }

}
