package vacancy.io;

import org.jsoup.Jsoup;
import vacancy.model.Skill;
import vacancy.model.Vacancy;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VacancyHtmlWriter {

    private static final String HTML_FILE_SUFFIX = ".html";

    private static final String HTML_SKILL_REPORT_HEAD = """
            <html lang="ru"><head>
            <meta charset="UTF-8"><title>%s</title>
            <style>
                body { font-family: system-ui, sans-serif; background: #fafbfc; color: #222; margin: 0; padding: 32px; }
                h1 { font-size: 2em; margin-bottom: 1em; }
                table { border-collapse: collapse; width: 80%%; margin: auto; background: #fff; box-shadow: 0 2px 18px #0001; }
                th, td { padding: 10px 18px; border-bottom: 1px solid #eee; text-align: left; }
                th { background: #f4f7fb; }
                tr:hover { background: #f9f6ff; }
            </style>
            </head><body>
            <h1>%s</h1>
            <table>
                <tr><th>#</th><th>Навык</th><th>Вакансий</th></tr>
            """;

    private static final String HTML_VACANCY_TABLE_HEAD = """
            <html lang="ru"><head>
            <meta charset="UTF-8"><title>Список всех найденных вакансий</title>
            <style>
                body { font-family: system-ui, sans-serif; background: #fafbfc; color: #222; margin: 0; padding: 32px; }
                h1 { font-size: 2em; margin-bottom: 1em; }
                table { border-collapse: collapse; width: 99%%; background: #fff; box-shadow: 0 2px 18px #0001; }
                th, td { padding: 8px 10px; border-bottom: 1px solid #eee; text-align: left; vertical-align: top;}
                th { background: #f4f7fb; position: sticky; top: 0; }
                tr:hover { background: #f9f6ff; }
                .skills { font-size: 0.96em; color: #363; }
                .id { color: #888; font-size: 0.94em; }
                a { color: #3949ab; text-decoration: none; }
                a:hover { text-decoration: underline; }
            </style>
            </head><body>
            <h1>Все найденные по запросам вакансии</h1>
            <table>
              <tr>
                <th>#</th>
                <th>ID</th>
                <th>Название</th>
                <th>Компания</th>
                <th>Локация</th>
                <th>Зарплата</th>
                <th>Навыки</th>
                <th>URL</th>
                <th>Источник</th>
                <th>Дата публикации</th>
              </tr>
            """;

    private static final String HTML_TABLE_TAIL = """
            </table>
            </body></html>
            """;

    public static void writeSkillFrequencyReport(Map<Skill, Integer> freqMap, String baseFileName, int topN) {
        String fileName = baseFileName.endsWith(HTML_FILE_SUFFIX) ? baseFileName : baseFileName + HTML_FILE_SUFFIX;
        List<Map.Entry<Skill, Integer>> topSkills = getSortedSkillEntries(freqMap)
                .stream().limit(topN).toList();
        writeSkillFrequencyHtml(topSkills, fileName, "Топ востребованных скиллов по HeadHunter");
    }

    public static void writeAllSkillFrequencyReport(Map<Skill, Integer> freqMap, String filename) {
        List<Map.Entry<Skill, Integer>> allSkills = getSortedSkillEntries(freqMap);
        writeSkillFrequencyHtml(allSkills, filename, "Все скиллы, встречающиеся в вакансиях (отсортировано по частоте)");
    }

    private static List<Map.Entry<Skill, Integer>> getSortedSkillEntries(Map<Skill, Integer> freqMap) {
        return freqMap.entrySet().stream()
                .sorted(Map.Entry.<Skill, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(e -> e.getKey().getName()))
                .toList();
    }

    private static void writeSkillFrequencyHtml(List<Map.Entry<Skill, Integer>> skills,
                                                String baseFileName, String title) {
        String fileName = baseFileName.endsWith(HTML_FILE_SUFFIX) ? baseFileName : baseFileName + HTML_FILE_SUFFIX;
        StringBuilder html = new StringBuilder(HTML_SKILL_REPORT_HEAD.formatted(escape(title), escape(title)));
        int pos = 1;
        for (Map.Entry<Skill, Integer> entry : skills) {
            html.append("<tr>")
                    .append("<td>").append(pos++).append("</td>")
                    .append("<td>").append(escape(entry.getKey().getName())).append("</td>")
                    .append("<td>").append(entry.getValue()).append("</td>")
                    .append("</tr>");
        }
        html.append(HTML_TABLE_TAIL);

        try {
            Files.writeString(Paths.get(fileName), html.toString());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при записи HTML-отчёта: " + e.getMessage(), e);
        }
    }

    public static void writeVacancyTableReport(List<Vacancy> vacancies, String baseFileName) {
        String fileName = baseFileName.endsWith(HTML_FILE_SUFFIX) ? baseFileName : baseFileName + HTML_FILE_SUFFIX;
        StringBuilder html = new StringBuilder(HTML_VACANCY_TABLE_HEAD);
        int pos = 1;
        for (Vacancy v : vacancies) {
            html.append("<tr>")
                    .append("<td>").append(pos++).append("</td>")
                    .append("<td class='id'>").append(escape(v.getId())).append("</td>")
                    .append("<td>").append(escape(v.getTitle())).append("</td>")
                    .append("<td>").append(escape(v.getCompany())).append("</td>")
                    .append("<td>").append(escape(v.getLocation())).append("</td>")
                    .append("<td>").append(formatSalary(v.getSalary())).append("</td>")
                    .append("<td class='skills'>").append(skillsToString(v.getSkills())).append("</td>")
                    .append("<td>").append(v.getUrl() != null && !v.getUrl().isBlank()
                            ? "<a href='" + escape(v.getUrl()) + "' target='_blank'>ссылка</a>" : "").append("</td>")
                    .append("<td>").append(escape(v.getSource())).append("</td>")
                    .append("<td>").append(escape(v.getPublishedAt())).append("</td>")
                    .append("</tr>\n");
        }

        html.append(HTML_TABLE_TAIL);

        try {
            Files.writeString(Paths.get(fileName), html.toString());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при записи HTML-отчёта по вакансиям: " + e.getMessage(), e);
        }
    }

    private static String skillsToString(java.util.Set<Skill> skills) {
        if (skills == null || skills.isEmpty()) return "";
        return skills.stream()
                .map(Skill::getName)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    /**
     * Форматирует строку зарплаты в красивый вид.
     * Ожидается JSON-строка вида {"from":170000,"to":200000,"currency":"RUR","gross":false}
     */
    private static String formatSalary(String salaryJson) {
        if (salaryJson == null || salaryJson.isBlank() || salaryJson.equals("null")) return "";
        try {
            String from = null, to = null, cur = null;
            boolean gross = false;
            for (String part : salaryJson.replace("{", "").replace("}", "").replace("\"", "").split(",")) {
                String[] kv = part.split(":", 2);
                if (kv.length != 2) continue;
                String key = kv[0].trim();
                String val = kv[1].trim();
                switch (key) {
                    case "from" -> from = val.equals("null") ? null : val;
                    case "to" -> to = val.equals("null") ? null : val;
                    case "currency" -> cur = val;
                    case "gross" -> gross = Boolean.parseBoolean(val);
                }
            }
            String curSign = switch (cur) {
                case "RUR", "RUB" -> "₽";
                case "USD" -> "$";
                case "EUR" -> "€";
                default -> cur != null ? cur : "";
            };
            StringBuilder sb = new StringBuilder();
            if (from != null && to != null)
                sb.append(String.format("%,d–%,d %s", Integer.parseInt(from), Integer.parseInt(to), curSign));
            else if (from != null)
                sb.append("от ").append(String.format("%,d %s", Integer.parseInt(from), curSign));
            else if (to != null)
                sb.append("до ").append(String.format("%,d %s", Integer.parseInt(to), curSign));
            if (gross)
                sb.append(" (до вычета налогов)");
            return sb.toString();
        } catch (Exception e) {
            return salaryJson;
        }
    }

    /**
     * Экранирует спецсимволы для HTML (без кавычек).
     */
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /**
     * Очищает HTML-теги.
     */
    private static String htmlToPlainText(String html) {
        if (html == null || html.isBlank()) return "";
        return Jsoup.parse(html).text();
    }

}
