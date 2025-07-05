package vacancy.service;

import vacancy.model.Skill;
import vacancy.model.Vacancy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис для анализа частотности встречаемости скиллов в вакансиях.
 */
public class VacancyFrequencyAnalyzer {

    /**
     * Строит частотный словарь: навык -> количество вакансий, в которых он встречается.
     */
    public Map<Skill, Integer> buildSkillFrequency(List<Vacancy> vacancies) {
        Map<Skill, Integer> freq = new HashMap<>();
        for (Vacancy vacancy : vacancies) {
            if (vacancy.getSkills() != null) {
                for (Skill skill : vacancy.getSkills()) {
                    freq.put(skill, freq.getOrDefault(skill, 0) + 1);
                }
            }
        }
        return freq;
    }

    /**
     * Возвращает топ-N самых популярных скиллов по количеству вакансий.
     */
    public List<Map.Entry<Skill, Integer>> getTopSkills(Map<Skill, Integer> freqMap, int topN) {
        return freqMap.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(topN)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает частотный словарь (Map<String, Integer>), если нужен именно нормализованный текст.
     */
    public Map<String, Integer> buildSkillNameFrequency(List<Vacancy> vacancies) {
        Map<String, Integer> freq = new HashMap<>();
        for (Vacancy vacancy : vacancies) {
            if (vacancy.getSkills() != null) {
                for (Skill skill : vacancy.getSkills()) {
                    String name = skill.getName();
                    freq.put(name, freq.getOrDefault(name, 0) + 1);
                }
            }
        }
        return freq;
    }

    /**
     * Строит матрицу "Скилл — количество упоминаний в источниках" (например, по разным job-сайтам).
     *
     * @param vacancies Список вакансий
     * @return Map<Skill, Map < String, Integer>>, где String — источник (например, "HeadHunter", "SuperJob")
     */
    public Map<Skill, Map<String, Integer>> buildSkillSourceMatrix(List<Vacancy> vacancies) {
        Map<Skill, Map<String, Integer>> result = new HashMap<>();
        for (Vacancy vacancy : vacancies) {
            if (vacancy.getSkills() != null) {
                for (Skill skill : vacancy.getSkills()) {
                    result.putIfAbsent(skill, new HashMap<>());
                    Map<String, Integer> bySource = result.get(skill);
                    String source = vacancy.getSource() == null ? "unknown" : vacancy.getSource();
                    bySource.put(source, bySource.getOrDefault(source, 0) + 1);
                }
            }
        }
        return result;
    }

}
