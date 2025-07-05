package vacancy.fetch.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class VacancySearchRequestDto {

    /**
     * Текст поиска (например, "Java").
     */
    String query;

    /**
     * Регион (например, "1" — Москва, "113" — Россия).
     */
    String area;

    /**
     * Сколько на страницу (до 100).
     */
    int perPage;

    /**
     * Максимум страниц (защитный лимит).
     */
    int maxPages;

    /**
     * Дата "с".
     */
    LocalDate dateFrom;

    /**
     * Дата "по".
     */
    LocalDate dateTo;

    /**
     * Наименование файла для сохранения результата.
     */
    String fileName;

}