package fgos.parser.impl


import fgos.model.FGOSCompetency
import fgos.parser.FGOSParserBase
import groovy.util.logging.Slf4j
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

import java.util.regex.Pattern

/**
 *  Парсер для старых версий ФГОС ВО.
 */
@Slf4j
class FGOSParserOld extends FGOSParserBase {

    public static final List<String> DEMO_SOURCES = [
            "https://fgos.ru/fgos/fgos-02-03-01-matematika-i-kompyuternye-nauki-807/",
            "https://fgos.ru/fgos/fgos-09-03-04-programmnaya-inzheneriya-229/",
            "https://fgos.ru/fgos/fgos-09-03-03-prikladnaya-informatika-207/",
            "https://fgos.ru/fgos/fgos-02-03-03-matematicheskoe-obespechenie-i-administrirovanie-informacionnyh-sistem-222/",
    ]

    private static final String DESC_REGEX = /(.+?)\s*\(([ОоПпКк]{2,4}-\d+)\)(?:;|\.|\n|$)/
    private static final Pattern DESC_RE = Pattern.compile(DESC_REGEX)
    private static final String BLOCK_MARKERS = /компетенциями:|деятельность:/

    @Override
    List<FGOSCompetency> parseCompetenciesFromSource(String url) {
        log.info("Parse of ${url}")

        Document html = Jsoup.connect(url).timeout(20000).get()
        String directionName = parseDirectionName(html)
        if (!directionName) directionName = url

        Element container = html.select('article').first() ?:
                html.select('div.entry-content').first() ?:
                        html.select('main').first() ?:
                                html
        String text = container.text()
        Set<String> seen = new HashSet<>()
        List<FGOSCompetency> result = []

        def blocks = text.split("(?i)$BLOCK_MARKERS")
        def blockTitles = []
        def m = (text =~ /(?i)($BLOCK_MARKERS)/)
        while (m.find()) {
            blockTitles << m.group(1)
        }

        for (int i = 1; i < blocks.size(); i++) { // первый блок — до компетенций
            def block = blocks[i]
            def matcher = DESC_RE.matcher(block)
            while (matcher.find()) {
                String desc = matcher.group(1)?.replaceAll(/^[\s.,–—-]+|[\s.,–—-]+$/, "")
                desc = normalizeStart(desc)
                String code = matcher.group(2)?.toUpperCase()
                if (desc && desc.size() > 2 && desc.size() < 500 && !seen.contains(code)) {
                    result.add(new FGOSCompetency(
                            code,
                            desc,
                            "", // индикатор для старых ФГОСов не нужен
                            directionName,
                            url
                    ))
                    seen.add(code)
                }
            }
        }
        return result
    }

    // Корректируем первое слово — убираем одну последнюю букву
    static String normalizeStart(String s) {
        if (!s) return s
        s = s.trim()
        def words = s.split(/\s+/, 2)
        if (words.size() > 1) {
            def first = words[0]
            if (first.size() > 1) {
                first = first[0..-2] // просто убираем последнюю букву
            }
            return first.capitalize() + " " + words[1]
        }
        return s.capitalize()
    }

}
