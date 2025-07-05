package vacancy.parser.impl;

import com.fasterxml.jackson.databind.JsonNode;
import vacancy.model.Skill;
import vacancy.model.Vacancy;
import vacancy.parser.VacancyDetailParser;

import java.util.HashSet;
import java.util.Set;

/**
 * Парсит JsonNode от HH API в Vacancy.
 */
public class HHJsonVacancyDetailParser implements VacancyDetailParser {

    @Override
    public Vacancy parse(JsonNode vacancyJson) {
        if (vacancyJson == null || vacancyJson.isEmpty()) return null;

        Set<Skill> skills = new HashSet<>();
        JsonNode keySkillsNode = vacancyJson.path("key_skills");
        if (keySkillsNode.isArray()) {
            for (JsonNode skillNode : keySkillsNode) {
                skills.add(new Skill(skillNode.path("name").asText()));
            }
        }

        return Vacancy.builder()
                .id(vacancyJson.path("id").asText())
                .title(vacancyJson.path("name").asText())
                .company(vacancyJson.path("employer").path("name").asText())
                .location(vacancyJson.path("area").path("name").asText())
                .salary(vacancyJson.has("salary") && !vacancyJson.path("salary").isNull() ?
                        vacancyJson.path("salary").toString() : null)
                .url(vacancyJson.path("alternate_url").asText())
                .description(vacancyJson.path("description").asText())
                .skills(skills)
                .source("HeadHunter")
                .publishedAt(vacancyJson.path("published_at").asText())
                .build();
    }

}
