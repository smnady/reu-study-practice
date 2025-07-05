package profstandart.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProfStandart {
    private final String code;
    private final String name;
    private final List<ProfStandartOTF> otfs = new ArrayList<>();

    public ProfStandart(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public ProfStandart addOtf(ProfStandartOTF otf) {
        otfs.add(otf);
        return this;
    }

}