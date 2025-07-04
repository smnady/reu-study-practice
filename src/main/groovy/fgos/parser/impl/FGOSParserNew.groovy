package fgos.parser.impl


import fgos.model.FGOSCompetency
import fgos.parser.FGOSParserBase
import groovy.util.logging.Slf4j
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Парсер для обновленных ФГОС ВО (с наиболее свежей датой редактирования приказом Минобрнауки России).
 */
@Slf4j
class FGOSParserNew extends FGOSParserBase {

    public static final List<String> DEMO_SOURCES = [
            "https://fgos.ru/fgos/fgos-02-03-01-matematika-i-kompyuternye-nauki-807/",
            "https://fgos.ru/fgos/fgos-02-03-03-matematicheskoe-obespechenie-i-administrirovanie-informacionnyh-sistem-809/",
            "https://fgos.ru/fgos/fgos-02-03-02-fundamentalnaya-informatika-i-informacionnye-tehnologii-808/",
    ]

    @Override
    List<FGOSCompetency> parseCompetenciesFromSource(String url) {
        log.info("Parse of ${url}")
        Document doc = Jsoup.connect(url).timeout(20000).get()
        String directionName = parseDirectionName(doc)
        if (!directionName) directionName = url

        String indicatorText = parseIndicatorText(doc)
        List<FGOSCompetency> result = []

        doc.select("table").each { table ->
            def headerCells = table.select("tr").first()?.select("th,td")*.text()*.toLowerCase()
            int compIdx = headerCells.findIndexOf {
                it ==~ /код и наименование .+ компетенции выпускника/
            }
            if (compIdx != -1) {
                table.select("tr").drop(1).each { row ->
                    def tds = row.select("td")
                    if (tds.size() == 0) return // строка полностью пустая

                    // компетенция всегда во втором td, а если он один — в нем
                    def compCell = tds.size() > 1 ? tds[1] : tds[0]
                    def cellText = compCell.text().trim()
                    if (cellText) {
                        def comp = parseCodeAndDesc(cellText)
                        result << new FGOSCompetency(
                                comp[0],
                                comp[1],
                                indicatorText,
                                directionName,
                                url
                        )
                    }
                }
            }
        }
        return result
    }

    private static List<String> parseCodeAndDesc(String string) {
        if (!string) return ["", ""]
        def matcher = (string =~ /^([А-ЯA-ZЁ\-]+[-–]\d+)[. ]+(.*)$/)
        if (matcher.find()) {
            def description = replaceFirstWordIfNecessary(matcher.group(2)?.trim())
            return [matcher.group(1), description]
        }
        def idx = string.indexOf(' ')
        if (idx > 0) {
            def description = replaceFirstWordIfNecessary(string[idx + 1..-1])
            return [string[0..idx - 1], description]
        }
        return [string, ""]
    }

    private static String parseIndicatorText(Document doc) {
        def el = doc.select("div, p, li").find {
            it.text().trim().startsWith("3.7.")
        }
        return el ? el.text().replaceAll("\\s+", " ").trim() : ""
    }

    /**
     * Проверяет, если первое слово "Способен", то заменяет его на "Способность".
     *
     * @param description строка с описанием компетенции
     * @return обработанная строка
     */
    private static String replaceFirstWordIfNecessary(String description) {
        if (description?.startsWith("Способен")) {
            return description.replaceFirst(/^Способен/, "Способность")
        }
        return description
    }

}
