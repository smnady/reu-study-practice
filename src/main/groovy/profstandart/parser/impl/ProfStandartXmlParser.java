package profstandart.parser.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import profstandart.model.ProfStandart;
import profstandart.model.ProfStandartOTF;
import profstandart.model.ProfStandartTD;
import profstandart.parser.ProfStandartParser;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

import static profstandart.parser.impl.ProfStandartXmlParser.Tags.PROFESSIONAL_STANDART;

public class ProfStandartXmlParser implements ProfStandartParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfStandartXmlParser.class);

    public static final class Tags {

        public static final String PROFESSIONAL_STANDART = "ProfessionalStandart";
        public static final String REGISTRATION_NUMBER = "RegistrationNumber";
        public static final String NAME_PROFESSIONAL_STANDART = "NameProfessionalStandart";
        public static final String GENERALIZED_WORK_FUNCTION = "GeneralizedWorkFunction";
        public static final String CODE_OTF = "CodeOTF";
        public static final String NAME_OTF = "NameOTF";
        public static final String LEVEL_OF_QUALIFICATION = "LevelOfQualification";
        public static final String PARTICULAR_WORK_FUNCTION = "ParticularWorkFunction";
        public static final String CODE_TF = "CodeTF";
        public static final String NAME_TF = "NameTF";
        public static final String LABOR_ACTION = "LaborAction";

        private Tags() {
        }
    }

    @Override
    public ProfStandart parse(String xmlPath) throws Exception {
        LOGGER.info("Parsing of {}", xmlPath);
        File file = new File(xmlPath);
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        doc.getDocumentElement().normalize();

        Node profStdNode = doc.getElementsByTagName(PROFESSIONAL_STANDART).item(0);
        if (profStdNode == null)
            throw new RuntimeException("Не найден <" + PROFESSIONAL_STANDART + "> в файле " + xmlPath);
        Element profStdElem = (Element) profStdNode;

        String code = getTextByTag(profStdElem, Tags.REGISTRATION_NUMBER);
        String name = getTextByTag(profStdElem, Tags.NAME_PROFESSIONAL_STANDART);

        ProfStandart standart = new ProfStandart(code, name);

        NodeList gwfNodes = profStdElem.getElementsByTagName(Tags.GENERALIZED_WORK_FUNCTION);
        for (int i = 0; i < gwfNodes.getLength(); i++) {
            Element otfElem = (Element) gwfNodes.item(i);
            String otfCode = getTextByTag(otfElem, Tags.CODE_OTF);
            String otfName = getTextByTag(otfElem, Tags.NAME_OTF);
            String otfLevel = getTextByTag(otfElem, Tags.LEVEL_OF_QUALIFICATION);

            ProfStandartOTF otf = new ProfStandartOTF(otfCode, otfName, otfLevel);

            NodeList tdNodes = otfElem.getElementsByTagName(Tags.PARTICULAR_WORK_FUNCTION);
            for (int j = 0; j < tdNodes.getLength(); j++) {
                Element tdElem = (Element) tdNodes.item(j);
                String tdCode = getTextByTag(tdElem, Tags.CODE_TF);
                String tdName = getTextByTag(tdElem, Tags.NAME_TF);

                ProfStandartTD td = new ProfStandartTD(tdCode, tdName);

                NodeList laborActionNodes = tdElem.getElementsByTagName(Tags.LABOR_ACTION);
                for (int k = 0; k < laborActionNodes.getLength(); k++) {
                    String laName = laborActionNodes.item(k).getTextContent().trim();
                    if (!laName.isEmpty()) {
                        otf.addAction(new ProfStandartTD(null, laName));
                    }
                }
                otf.addAction(td);
            }
            standart.addOtf(otf);
        }
        return standart;
    }

    private static String getTextByTag(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent().trim();
        }
        return "";
    }

}
