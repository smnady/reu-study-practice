package vacancy.parser;

import com.fasterxml.jackson.databind.JsonNode;
import vacancy.model.Vacancy;

/**
 * Парсер информации о вакансии из JSON-объекта в доменную модель Vacancy.
 */
public interface VacancyDetailParser {

    /**
     * Преобразует JSON вакансии (как возвращает HH API) в объект Vacancy с нужными нам полями.
     *
     * @param vacancyJson JsonNode (корневой объект вакансии)
     * @return Vacancy с заполненными полями (id, title, skills и др.)
     */
    Vacancy parse(JsonNode vacancyJson);

}
