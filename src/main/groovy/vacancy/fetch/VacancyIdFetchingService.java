package vacancy.fetch;

import vacancy.fetch.dto.VacancySearchRequestDto;
import java.util.List;

/**
 * Сервис для получения идентификаторов вакансий по заданному поисковому запросу.
 */
public interface VacancyIdFetchingService {

    /**
     * Получить список идентификаторов вакансий по заданному поисковому запросу.
     *
     * @param req параметры поиска (ключевые слова, регион, ограничения по страницам и т.д.)
     * @return список идентификаторов вакансий (id)
     * @throws Exception в случае ошибок при выполнении HTTP-запросов или парсинге
     */
    List<String> fetchVacancyIds(VacancySearchRequestDto req) throws Exception;

}
