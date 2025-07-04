package fgos.parser;


import fgos.model.FGOSCompetency;

import java.util.List;

public interface FGOSParser {

    List<FGOSCompetency> parse(List<String> fgosUrls);

}
