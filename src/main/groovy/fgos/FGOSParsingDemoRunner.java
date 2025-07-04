package fgos;


import fgos.html.FGOSHtmlWriter;
import fgos.parser.impl.FGOSParserNew;
import fgos.parser.impl.FGOSParserOld;

public class FGOSParsingDemoRunner {

    private static final String FILE_WITH_PARSING_RESULT_FOR_OLD_FGOS = "result_of_parse_old_fgos_competencies.html";
    private static final String FILE_WITH_PARSING_RESULT_FOR_NEW_FGOS = "result_of_parse_new_fgos_competencies.html";

    public static void main(String[] args) {
        var oldFgosParser = new FGOSParserOld();
        var newFgosParser = new FGOSParserNew();

        var resultWriter = new FGOSHtmlWriter();

        resultWriter.writeHtml(oldFgosParser.parse(FGOSParserOld.DEMO_SOURCES), FILE_WITH_PARSING_RESULT_FOR_OLD_FGOS);
        resultWriter.writeHtml(newFgosParser.parse(FGOSParserNew.DEMO_SOURCES), FILE_WITH_PARSING_RESULT_FOR_NEW_FGOS);
    }

}
