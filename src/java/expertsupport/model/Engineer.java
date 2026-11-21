package expertsupport.model;

import java.util.List;

/** Полевой инженер: создаёт карточки инцидентов (операция 1). */
public class Engineer extends User {

    public Engineer(int id, String fullName, String email) {
        super(id, fullName, email);
    }

    @Override
    public String getRole() {
        return "engineer";
    }

    /**
     * Операция 1 «Сформировать карточку инцидента».
     * В процедурной версии — функция create_incident().
     */
    public Incident reportIncident(int incidentId, Equipment equipment, String title, String description,
                                   String category, List<Skill> requiredSkills, PriorityLevel priority) {
        return new Incident(incidentId, equipment, this, title, description, category, requiredSkills, priority);
    }
}
