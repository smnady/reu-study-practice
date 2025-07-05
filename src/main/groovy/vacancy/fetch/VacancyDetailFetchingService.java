package vacancy.fetch;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Сервис для получения подробной информации по вакансии по её идентификатору.
 */
public interface VacancyDetailFetchingService {

    /**
     * Получить JSON-описание вакансии по её идентификатору.
     *
     * @param vacancyId идентификатор вакансии (строка)
     * @return JsonNode с полной структурой вакансии (как возвращает HH API)
     * @throws Exception в случае ошибки HTTP-запроса или парсинга ответа
     */
    JsonNode fetchVacancyJson(String vacancyId) throws Exception;

}
