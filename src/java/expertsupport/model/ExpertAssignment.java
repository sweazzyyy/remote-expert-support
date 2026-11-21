package expertsupport.model;

import java.time.LocalDateTime;

/** Сущность EXPERT_ASSIGNMENT — назначение эксперта на инцидент. */
public class ExpertAssignment {
    private final int id;
    private final Incident incident;
    private final Expert expert;
    private final double score;
    private final LocalDateTime assignedAt;

    ExpertAssignment(int id, Incident incident, Expert expert, double score) {
        this.id = id;
        this.incident = incident;
        this.expert = expert;
        this.score = score;
        this.assignedAt = LocalDateTime.now();
    }

    /**
     * Операция 4 «Установить P2P WebRTC-сессию».
     * В процедурной версии — функция setup_webrtc_session().
     */
    public WebRtcSession openSession(int sessionId) {
        return new WebRtcSession(sessionId, this);
    }

    public int getId() {
        return id;
    }

    public Incident getIncident() {
        return incident;
    }

    public Expert getExpert() {
        return expert;
    }

    public double getScore() {
        return score;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
}
