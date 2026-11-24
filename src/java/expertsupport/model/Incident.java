package expertsupport.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/** Сущность INCIDENT; требуемые навыки — связь INCIDENT_SKILL. */
public class Incident {
    private final int id;
    private final Equipment equipment;
    private final Engineer engineer;
    private final String title;
    private final String description;
    private final String category;
    private final List<Skill> requiredSkills;
    private final PriorityLevel priority;
    private final LocalDateTime createdAt;
    private IncidentStatus status;

    Incident(int id, Equipment equipment, Engineer engineer, String title, String description, String category,
             List<Skill> requiredSkills, PriorityLevel priority) {
        if (requiredSkills.isEmpty()) {
            throw new IllegalArgumentException("Инцидент должен требовать хотя бы один навык");
        }
        this.id = id;
        this.equipment = equipment;
        this.engineer = engineer;
        this.title = title;
        this.description = description;
        this.category = category;
        // повторяющиеся навыки отбрасываются с сохранением порядка:
        // в модели данных пара (incident_id, skill_id) в INCIDENT_SKILL уникальна
        this.requiredSkills = new ArrayList<>(new LinkedHashSet<>(requiredSkills));
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
        this.status = IncidentStatus.CREATED;
    }

    /**
     * Фиксация результата подбора эксперта (EXPERT_ASSIGNMENT).
     * В процедурной версии — функция create_assignment().
     * Назначить эксперта можно только на новый инцидент (статус CREATED).
     */
    public ExpertAssignment assignTo(int assignmentId, Expert expert, double score) {
        if (status != IncidentStatus.CREATED) {
            throw new IllegalStateException("Инцидент " + id + " уже в статусе " + status
                    + ", повторное назначение эксперта невозможно");
        }
        status = IncidentStatus.ASSIGNED;
        return new ExpertAssignment(assignmentId, this, expert, score);
    }

    void markResolved() {
        status = IncidentStatus.RESOLVED;
    }

    public int getWeight() {
        return priority.getWeight();
    }

    public int getId() {
        return id;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public Engineer getEngineer() {
        return engineer;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public List<Skill> getRequiredSkills() {
        return Collections.unmodifiableList(requiredSkills);
    }

    public PriorityLevel getPriority() {
        return priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public IncidentStatus getStatus() {
        return status;
    }
}
