package vacancy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vacancy {
    private String id;
    private String title;
    private String company;
    private String location;
    private String salary;
    private String url;
    private String description;
    private Set<Skill> skills;
    private String source;
    private String publishedAt;

    public void setSkills(Set<Skill> skills) {
        if (skills != null) {
            this.skills = skills.stream()
                    .map(skill -> new Skill(skill.getName()))
                    .collect(Collectors.toSet());
        } else {
            this.skills = null;
        }
    }

}
