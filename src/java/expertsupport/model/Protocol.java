package expertsupport.model;

import java.time.LocalDateTime;

/** Сущность PROTOCOL — протокол экспертного заключения по сеансу. */
public class Protocol {
    private final int id;
    private final WebRtcSession session;
    private final String expertNotes;
    private final LocalDateTime closedAt;

    Protocol(int id, WebRtcSession session, String expertNotes) {
        this.id = id;
        this.session = session;
        this.expertNotes = expertNotes;
        this.closedAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public WebRtcSession getSession() {
        return session;
    }

    public String getExpertNotes() {
        return expertNotes;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }
}
