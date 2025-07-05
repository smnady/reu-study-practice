package vacancy.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vacancy.model.Skill;
import vacancy.model.Vacancy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;

/**
 * Утилита для выгрузки аналитики по вакансиям/скиллам в формате CSV.
 */
public class VacancyCsvWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(VacancyCsvWriter.class);

    private static final String CSV_FILE_SUFFIX = ".csv";

    private static final String CSV_ROW_TEMPLATE = "{0};{1}" + System.lineSeparator();

    /**
     * Выгружает частотность скиллов в CSV-файл: Навык;Частота
     */
    public static void writeSkillFrequencyCsv(Map<Skill, Integer> freqMap, String baseFileName) {
        String fileName = baseFileName.endsWith(CSV_FILE_SUFFIX) ? baseFileName : baseFileName + CSV_FILE_SUFFIX;
        LOGGER.info("Начинается выгрузка частотности скиллов в файл: {}", fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(format(CSV_ROW_TEMPLATE, "Навык", "Частота"));
            freqMap.entrySet().stream()
                    .sorted(Map.Entry.<Skill, Integer>comparingByValue(Comparator.reverseOrder())
                            .thenComparing(e -> e.getKey().getName()))
                    .forEach(entry -> {
                        try {
                            writer.write(format(CSV_ROW_TEMPLATE, entry.getKey().getName(), entry.getValue()));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            LOGGER.info("Успешно выгружено {} скиллов в файл: {}", freqMap.size(), fileName);
        } catch (IOException e) {
            LOGGER.error("Ошибка при записи CSV отчёта по скиллам: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при записи CSV отчёта по скиллам: " + e.getMessage(), e);
        }
    }

    /**
     * Выгружает CSV вида: vacancy_id;skill (для анализа совместной встречаемости скиллов и вакансий)
     */
    public static void writeVacancySkillPairs(List<Vacancy> vacancies, String baseFileName) {
        String fileName = baseFileName.endsWith(CSV_FILE_SUFFIX) ? baseFileName : baseFileName + CSV_FILE_SUFFIX;
        LOGGER.info("Начинается выгрузка vacancy-skill пар в файл: {}", fileName);
        int pairsCount = 0;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(format(CSV_ROW_TEMPLATE, "vacancy_id", "skill"));
            for (Vacancy vacancy : vacancies) {
                if (vacancy.getSkills() != null) {
                    for (Skill skill : vacancy.getSkills()) {
                        writer.write(format(CSV_ROW_TEMPLATE, vacancy.getId(), skill.getName()));
                        pairsCount++;
                    }
                }
            }
            LOGGER.info("Успешно выгружено {} пар vacancy-skill в файл: {}", pairsCount, fileName);
        } catch (IOException e) {
            LOGGER.error("Ошибка при записи vacancy-skill csv: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при записи vacancy-skill csv: " + e.getMessage(), e);
        }
    }

}
