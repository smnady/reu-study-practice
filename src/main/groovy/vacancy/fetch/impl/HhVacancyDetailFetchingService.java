package vacancy.fetch.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import vacancy.fetch.VacancyDetailFetchingService;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Получает подробный JSON по id вакансии с HH API.
 */
@Slf4j
public class HhVacancyDetailFetchingService implements VacancyDetailFetchingService {

    private static final String BASE_URL = "https://api.hh.ru/vacancies/";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public JsonNode fetchVacancyJson(String vacancyId) throws Exception {
        String urlStr = BASE_URL + vacancyId;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "VacancyDetailFetcherBot/1.0 (for study)");

        try (InputStream is = conn.getInputStream()) {
            JsonNode json = MAPPER.readTree(is);
            log.info("Fetched vacancy {} from HH", vacancyId);
            return json;
        }
    }

}
