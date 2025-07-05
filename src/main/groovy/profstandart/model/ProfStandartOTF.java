package profstandart.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class ProfStandartOTF {
    private final String code;
    private final String name;
    private final String level;
    private final List<ProfStandartTD> actions = new ArrayList<>();

    public ProfStandartOTF(String code, String name, String level) {
        this.code = code;
        this.name = name;
        this.level = level;
    }

    public List<ProfStandartTD> getActions() {
        return Collections.unmodifiableList(actions);
    }

    public ProfStandartOTF addAction(ProfStandartTD td) {
        actions.add(td);
        return this;
    }

    @Override
    public String toString() {
        return code + " (" + level + "): " + name;
    }

}
