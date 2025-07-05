package vacancy.fetch.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class VacancySearchRequestDto {
    /**
     * Текст поиска (например, "Java")
     */
    String query;

    /**
     * Регион (например, "1" — Москва, "113" — Россия)
     */
    String area;
    /**
     * Сколько на страницу (до 100)
     */
    int perPage;
    /**
     * максимум страниц (защитный лимит)
     */
    int maxPages;

    /**
     * дата "с"
     */
    LocalDate dateFrom;
    /**
     * дата "по"
     */
    LocalDate dateTo;
    /**
     * куда сохранять результат (можно null)
     */
    String fileName;
    @Builder.Default
    boolean saveId = true;
    @Builder.Default
    boolean saveTitle = true;
    @Builder.Default
    boolean saveEmployer = true;
    @Builder.Default
    boolean saveSnippet = false;      // snippet.requirement + snippet.responsibility
    @Builder.Default
    boolean saveAlternateUrl = true;
    @Builder.Default
    boolean saveKeySkills = false;
}