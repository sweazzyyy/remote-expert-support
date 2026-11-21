package expertsupport.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Удалённый эксперт. Навыки (связь USER_SKILL) хранятся списком,
 * доступность определяется флагом is_active.
 */
public class Expert extends User {
    private final List<Skill> skills;
    private final double rating;
    private boolean active;

    public Expert(int id, String fullName, String email, List<Skill> skills, double rating, boolean active) {
        super(id, fullName, email);
        this.skills = new ArrayList<>(skills);
        this.rating = rating;
        this.active = active;
    }

    @Override
    public String getRole() {
        return "expert";
    }

    /** Эксперт доступен, только если активен (в отличие от базового User). */
    @Override
    public boolean isAvailable() {
        return active;
    }

    public List<Skill> getSkills() {
        return Collections.unmodifiableList(skills);
    }

    public double getRating() {
        return rating;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
