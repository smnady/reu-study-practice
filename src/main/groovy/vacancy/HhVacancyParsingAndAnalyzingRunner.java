package vacancy;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vacancy.fetch.VacancyDetailFetchingService;
import vacancy.fetch.VacancyIdFetchingService;
import vacancy.fetch.dto.VacancySearchRequestDto;
import vacancy.fetch.impl.HhVacancyDetailFetchingService;
import vacancy.fetch.impl.HhVacancyIdFetchingService;
import vacancy.io.VacancyCsvWriter;
import vacancy.model.Skill;
import vacancy.model.Vacancy;
import vacancy.parser.VacancyDetailParser;
import vacancy.parser.impl.HHJsonVacancyDetailParser;
import vacancy.service.VacancyFrequencyAnalyzer;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static vacancy.io.VacancyHtmlWriter.writeAllSkillFrequencyReport;
import static vacancy.io.VacancyHtmlWriter.writeSkillFrequencyReport;
import static vacancy.io.VacancyHtmlWriter.writeVacancyTableReport;

public class HhVacancyParsingAndAnalyzingRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(HhVacancyParsingAndAnalyzingRunner.class);
    private static final String HH_SKILLS_TOP_FREQUENCY_FILENAME = "hh_skills_top_frequency";
    private static final String HH_ALL_SKILLS_REPORT_FILENAME = "hh_all_skills";
    private static final String HH_VACANCIES_REPORT_FILENAME = "hh_all_vacancies";
    private static final String SKILLS_TO_FREQUENCY_FILENAME = "skills_to_frequency";
    private static final String VACANCY_TO_SKILL_FILENAME = "vacancy_to_skill";

    public static void main(String[] args) throws Exception {
        VacancyIdFetchingService idService = new HhVacancyIdFetchingService();
        VacancyDetailFetchingService detailService = new HhVacancyDetailFetchingService();
        VacancyDetailParser detailParser = new HHJsonVacancyDetailParser();

        // --- 1. Множество поисковых запросов ---
        List<VacancySearchRequestDto> requests = List.of(
                VacancySearchRequestDto.builder().query("java developer").perPage(80).maxPages(1).build(),
                VacancySearchRequestDto.builder().query("middle java developer").perPage(80).maxPages(1).build(),
                VacancySearchRequestDto.builder().query("senior java developer").perPage(80).maxPages(1).build(),
                VacancySearchRequestDto.builder().query("junior java developer").perPage(80).maxPages(1).build(),
                VacancySearchRequestDto.builder().query("бэкенд-разработчик на java").perPage(80).maxPages(1).build(),
                VacancySearchRequestDto.builder().query("программист на Java").perPage(80).maxPages(1).build(),
                VacancySearchRequestDto.builder().query("младший разработчик на Java").perPage(80).maxPages(1).build(),
                VacancySearchRequestDto.builder().query("ведущий разработчик на Java").perPage(80).maxPages(1).build()
        );

        Set<String> vacancyIds = new LinkedHashSet<>();
        for (VacancySearchRequestDto req : requests) {
            List<String> ids = idService.fetchVacancyIds(req);
            LOGGER.info("По запросу \"{}\" получено {} id вакансий", req.getQuery(), ids.size());
            vacancyIds.addAll(ids);
        }
        LOGGER.info("Уникальных id вакансий: {}", vacancyIds.size());

        // --- 2. Загружаем подробную информацию по каждой вакансии ---
        List<Vacancy> vacancies = vacancyIds.stream()
                .map(id -> {
                    try {
                        JsonNode vacancyJson = detailService.fetchVacancyJson(id);
                        return detailParser.parse(vacancyJson);
                    } catch (Exception e) {
                        LOGGER.error("Ошибка по вакансии {}: {}", id, e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        LOGGER.info("Вакансий всего: {}", vacancies.size());

        // --- 3. Анализ скиллов ---
        VacancyFrequencyAnalyzer analyzer = new VacancyFrequencyAnalyzer();
        Map<Skill, Integer> frequencyBySkill = analyzer.buildSkillFrequency(vacancies);

        VacancyCsvWriter.writeSkillFrequencyCsv(frequencyBySkill, SKILLS_TO_FREQUENCY_FILENAME);
        VacancyCsvWriter.writeVacancySkillPairs(vacancies, VACANCY_TO_SKILL_FILENAME);

        // --- 4. Топ-20 скиллов (консоль + HTML) ---
        int topN = 20;
        LOGGER.info("Топ-{} востребованных скиллов:", topN);
        List<Map.Entry<Skill, Integer>> topSkills = analyzer.getTopSkills(frequencyBySkill, topN);
        int pos = 1;
        for (Map.Entry<Skill, Integer> entry : topSkills) {
            LOGGER.info(format("%2d. %-22s — %3d", pos++, entry.getKey().getName(), entry.getValue()));
        }
        writeSkillFrequencyReport(frequencyBySkill, HH_SKILLS_TOP_FREQUENCY_FILENAME, topN);

        // --- 5. Все навыки по частоте (HTML) ---
        writeAllSkillFrequencyReport(frequencyBySkill, HH_ALL_SKILLS_REPORT_FILENAME);

        // --- 6. Таблица всех вакансий (HTML) ---
        writeVacancyTableReport(vacancies, HH_VACANCIES_REPORT_FILENAME);

        LOGGER.info("HTML-отчёты сохранены: {}, {}, {}",
                HH_SKILLS_TOP_FREQUENCY_FILENAME,
                HH_ALL_SKILLS_REPORT_FILENAME,
                HH_VACANCIES_REPORT_FILENAME
        );
    }

}
