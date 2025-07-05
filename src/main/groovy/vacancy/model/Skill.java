package vacancy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor
public class Skill {

    private String name;

    public Skill(String name) {
        this.name = normalize(name);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Skill skill)) return false;
        return Objects.equals(name, skill.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    private static String normalize(String s) {
        if (s == null) return "";
        return s.trim()
                .replaceAll("[.,;]+$", "")
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }

}