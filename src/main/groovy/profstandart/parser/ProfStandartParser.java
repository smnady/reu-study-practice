package profstandart.parser;

import profstandart.model.ProfStandart;

import java.util.ArrayList;
import java.util.List;

public interface ProfStandartParser {

    ProfStandart parse(String xmlPath) throws Exception;

    default List<ProfStandart> parse(List<String> xmlPathsToProfStandarts) throws Exception {
        List<ProfStandart> parsingResult = new ArrayList<>();
        for (String path : xmlPathsToProfStandarts) {
            parsingResult.add(parse(path));
        }
        return parsingResult;
    }

}
