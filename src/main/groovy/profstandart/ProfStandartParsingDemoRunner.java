package profstandart;

import profstandart.io.ProfStandardHtmlWriter;
import profstandart.model.ProfStandart;
import profstandart.parser.ProfStandartParser;
import profstandart.parser.impl.ProfStandartXmlParser;

import java.util.List;


public class ProfStandartParsingDemoRunner {

    private static final String RESULT_FILE = "parsed_profstandarts.html";
    private static final String PATH_TO_DIR = "src/main/resources/profstandart/";

    /**
     * @see <a href="https://profstandart.rosmintrud.ru/obshchiy-informatsionnyy-blok/natsionalnyy-reestr-professionalnykh-standartov/reestr-professionalnykh-standartov/index.php?ELEMENT_ID=61051">Разработчик Web и мультимедийных приложений</a>
     * @see <a href="https://profstandart.rosmintrud.ru/obshchiy-informatsionnyy-blok/natsionalnyy-reestr-professionalnykh-standartov/reestr-professionalnykh-standartov/index.php?ELEMENT_ID=57023">Архитектор программного обеспечения</a>
     * @see <a href="https://profstandart.rosmintrud.ru/obshchiy-informatsionnyy-blok/natsionalnyy-reestr-professionalnykh-standartov/reestr-professionalnykh-standartov/index.php?ELEMENT_ID=120722">Программист</a>
     */
    private static final List<String> PROF_STANDARTS_FOR_DEMO = List.of(
            PATH_TO_DIR + "ProfessionalStandarts_882.xml", // Разработчик Web и мультимедийных приложений
            PATH_TO_DIR + "ProfessionalStandarts_67.xml",  // Архитектор программного обеспечения
            PATH_TO_DIR + "ProfessionalStandarts_4.xml"    // Программист
    );

    public static void main(String[] args) throws Exception {
        ProfStandartParser parser = new ProfStandartXmlParser();
        List<ProfStandart> standards = parser.parse(PROF_STANDARTS_FOR_DEMO);

        ProfStandardHtmlWriter.writeHtml(standards, RESULT_FILE);
        System.out.println("Результат парсинга проф. стандартов записан в файл: " + RESULT_FILE);
    }

}
