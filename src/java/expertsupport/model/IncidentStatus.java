package expertsupport.model;

/** Статус инцидента (атрибут INCIDENT.status). */
public enum IncidentStatus {
    CREATED("created"),
    ASSIGNED("assigned"),
    RESOLVED("resolved");

    private final String code;

    IncidentStatus(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
