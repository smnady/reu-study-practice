package fgos.parser


import fgos.model.FGOSCompetency
import org.jsoup.nodes.Document

abstract class FGOSParserBase implements FGOSParser {

    abstract List<FGOSCompetency> parseCompetenciesFromSource(String url)

    List<FGOSCompetency> parse(List<String> fgosUrls) {
        List<FGOSCompetency> allCompetencies = []
        for (String url : fgosUrls) {
            allCompetencies.addAll(parseCompetenciesFromSource(url))
        }
        return allCompetencies
    }

    static String parseDirectionName(Document doc) {
        def h1 = doc.selectFirst("h1")?.text()?.trim()
        if (h1) return h1
        def strong = doc.selectFirst("strong")?.text()?.trim()
        if (strong) return strong
        return ""
    }

}
